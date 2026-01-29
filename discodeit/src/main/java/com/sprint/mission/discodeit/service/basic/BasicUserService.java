package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.*;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    private UserView toView(User user, UserStatus status) {
        boolean online = status != null && status.isOnlineNow();
        Instant lastSeenAt = status != null ? status.getLastSeenAt() : null;

        return new UserView(
                user.getId(),
                user.getDisplayName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getProfileImageId(),
                online,
                lastSeenAt
        );
    }

    @Override
    public User create(String displayName, String email, String phoneNumber) {
        // 입력 검증
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("displayName must not be blank");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email must not be blank");
        }
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("phoneNumber must not be blank");
        }

        // 중복 정책: displayName, email
        if (userRepository.existsByDisplayName(displayName)) {
            throw new IllegalArgumentException("DisplayName already exists: " + displayName);
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        User user = new User(displayName, email, phoneNumber);
        User saved = userRepository.save(user);

        // UserStatus 생성
        UserStatus status = new UserStatus(UUID.randomUUID(), saved.getId(), Instant.now());
        userStatusRepository.save(status);

        return saved;
    }

    @Override
    public UserView update(UserUpdateRequest request) {
        if (request == null || request.userId() == null || request.user() == null) {
            throw new IllegalArgumentException("request.userId and request.user are required");
        }

        UUID userId = request.userId();
        UserUpdateParams p = request.user();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found. id=" + userId));

        // displayName 중복 체크 (본인 제외) - 변경 시에만
        if (p.displayName() != null) {
            if (p.displayName().isBlank()) throw new IllegalArgumentException("displayName must not be blank");
            if (!p.displayName().equals(user.getDisplayName())) {
                userRepository.findByDisplayName(p.displayName())
                        .filter(found -> !found.getId().equals(userId))
                        .ifPresent(found -> { throw new IllegalArgumentException("DisplayName already exists: " + p.displayName()); });
            }
        }

        // email 중복 체크 (본인 제외) - 변경 시에만
        if (p.email() != null) {
            if (p.email().isBlank()) throw new IllegalArgumentException("email must not be blank");
            if (!p.email().equals(user.getEmail())) {
                userRepository.findByEmail(p.email())
                        .filter(found -> !found.getId().equals(userId))
                        .ifPresent(found -> { throw new IllegalArgumentException("Email already exists: " + p.email()); });
            }
        }

        // phoneNumber는 기존 정책(있으면 중복 체크)
        if (p.phoneNumber() != null) {
            if (p.phoneNumber().isBlank()) throw new IllegalArgumentException("phoneNumber must not be blank");
            if (!p.phoneNumber().equals(user.getPhoneNumber())) {
                if (userRepository.existsByPhoneNumber(p.phoneNumber())) {
                    throw new IllegalArgumentException("Phone number already exists: " + p.phoneNumber());
                }
            }
        }

        user.update(p.displayName(), p.email(), p.phoneNumber());

        // 프로필 이미지 대체
        ProfileImageParams img = request.profileImage();
        if (img != null) {
            validateProfileImage(img);

            UUID oldProfileImageId = user.getProfileImageId();

            BinaryContent binary = new BinaryContent(UUID.randomUUID(), img.bytes(), img.contentType(), img.filename());
            binaryContentRepository.save(binary);

            user.changeProfileImage(binary.getId());

            // "대체" 의미를 살리기 위해 기존 대표 이미지는 삭제(없으면 스킵)
            if (oldProfileImageId != null) {
                binaryContentRepository.deleteById(oldProfileImageId);
            }
        }

        User saved = userRepository.save(user);
        UserStatus status = userStatusRepository.findByUserId(userId).orElse(null);
        return toView(saved, status);
    }

    private void validateProfileImage(ProfileImageParams img) {
        if (img.bytes() == null || img.bytes().length == 0) {
            throw new IllegalArgumentException("profileImage.bytes is required");
        }
        if (img.contentType() == null || img.contentType().isBlank()) {
            throw new IllegalArgumentException("profileImage.contentType is required");
        }
        if (img.filename() == null || img.filename().isBlank()) {
            throw new IllegalArgumentException("profileImage.filename is required");
        }
    }

    @Override
    public UserView findById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found. id=" + userId));

        UserStatus status = userStatusRepository.findByUserId(userId).orElse(null);
        return toView(user, status);
    }

    @Override
    public List<UserView> findAll() {
        List<User> users = userRepository.findAll();

        Map<UUID, UserStatus> statusMap = userStatusRepository.findAll().stream()
                .collect(Collectors.toMap(UserStatus::getUserId, s -> s, (a, b) -> a));

        return users.stream()
                .map(u -> toView(u, statusMap.get(u.getId())))
                .toList();
    }

    @Override
    public void delete(UserDeleteRequest request) {
        if (request == null || request.userId() == null) {
            throw new IllegalArgumentException("request.userId is required");
        }

        UUID userId = request.userId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found. id=" + userId));

        // UserStatus 삭제(있으면)
        userStatusRepository.findByUserId(userId)
                .ifPresent(status -> userStatusRepository.deleteById(status.getId()));

        // 프로필 BinaryContent 삭제(대표 1개)
        UUID profileImageId = user.getProfileImageId();
        if (profileImageId != null) {
            binaryContentRepository.deleteById(profileImageId);
        }

        userRepository.deleteById(userId);
    }

    @Override
    public boolean existsById(UUID userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

}