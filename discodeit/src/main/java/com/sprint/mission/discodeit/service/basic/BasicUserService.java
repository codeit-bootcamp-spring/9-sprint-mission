package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.ProfileImageParams;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDeleteRequest;
import com.sprint.mission.discodeit.dto.user.UserParams;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserView;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    private UserView toView(User user, UserStatus status) {
        boolean online = status != null && status.isOnlineNow();
        Instant lastSeenAt = status != null ? status.getLastSeenAt() : null;

        return new UserView(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getDisplayName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getProfileImageId(),
                online,
                lastSeenAt
        );
    }

    @Override
    public UserView create(UserCreateRequest request) {
        if (request == null || request.user() == null) {
            throw new IllegalArgumentException("request.user is required");
        }

        UserParams p = request.user();

        String username = p.username();
        String displayName = p.displayName();
        String email = p.email();
        String phoneNumber = p.phoneNumber();
        String password = p.password();

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username must not be blank");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email must not be blank");
        }
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("phoneNumber must not be blank");
        }
        if (password != null && password.isBlank()) {
            throw new IllegalArgumentException("password must not be blank");
        }

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("Phone number already exists: " + phoneNumber);
        }

        User user = new User(username, email, phoneNumber, password);
        if (displayName != null && !displayName.isBlank()) {
            user.update(displayName, null, null);
        }

        User saved = userRepository.save(user);

        ProfileImageParams img = request.profileImage();
        if (img != null) {
            validateProfileImage(img);

            BinaryContent binary = new BinaryContent(
                    UUID.randomUUID(),
                    img.bytes(),
                    img.contentType(),
                    img.filename()
            );
            binaryContentRepository.save(binary);

            saved.changeProfileImage(binary.getId());
            saved = userRepository.save(saved);
        }

        UserStatus status = new UserStatus(UUID.randomUUID(), saved.getId(), Instant.now());
        userStatusRepository.save(status);

        return toView(saved, status);
    }

    @Override
    public UserView update(UserUpdateRequest request) {
        if (request == null || request.userId() == null || request.params() == null || request.params().user() == null) {
            throw new IllegalArgumentException("request.userId and request.params.user are required");
        }

        UUID userId = request.userId();
        UserUpdateRequest.UserFields p = request.params().user();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found. id=" + userId));

        if (p.displayName() != null && p.displayName().isBlank()) {
            throw new IllegalArgumentException("displayName must not be blank");
        }

        if (p.email() != null) {
            if (p.email().isBlank()) throw new IllegalArgumentException("email must not be blank");
            if (!p.email().equals(user.getEmail())) {
                userRepository.findByEmail(p.email())
                        .filter(found -> !found.getId().equals(userId))
                        .ifPresent(found -> {
                            throw new IllegalArgumentException("Email already exists: " + p.email());
                        });
            }
        }

        if (p.phoneNumber() != null) {
            if (p.phoneNumber().isBlank()) throw new IllegalArgumentException("phoneNumber must not be blank");
            if (!p.phoneNumber().equals(user.getPhoneNumber())) {
                if (userRepository.existsByPhoneNumber(p.phoneNumber())) {
                    throw new IllegalArgumentException("Phone number already exists: " + p.phoneNumber());
                }
            }
        }

        user.update(p.displayName(), p.email(), p.phoneNumber());
        if (p.password() != null) {
            if (p.password().isBlank()) {
                throw new IllegalArgumentException("password must not be blank");
            }

            if (user.getPassword() != null) {
                if (p.currentPassword() == null || p.currentPassword().isBlank()) {
                    throw new IllegalArgumentException("currentPassword is required");
                }
                if (!Objects.equals(user.getPassword(), p.currentPassword())) {
                    throw new IllegalArgumentException("currentPassword mismatch");
                }
            }

            user.changePassword(p.password());
        }

        ProfileImageParams img = request.params().profileImage();
        if (img != null) {
            validateProfileImage(img);

            UUID oldProfileImageId = user.getProfileImageId();

            BinaryContent binary = new BinaryContent(UUID.randomUUID(), img.bytes(), img.contentType(), img.filename());
            binaryContentRepository.save(binary);

            user.changeProfileImage(binary.getId());

            if (oldProfileImageId != null) {
                binaryContentRepository.delete(oldProfileImageId);
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
    public UserView findByUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username must not be blank");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found. username=" + username));

        UserStatus status = userStatusRepository.findByUserId(user.getId()).orElse(null);
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

        userStatusRepository.findByUserId(userId)
                .ifPresent(status -> userStatusRepository.delete(status.getId()));

        UUID profileImageId = user.getProfileImageId();
        if (profileImageId != null) {
            binaryContentRepository.delete(profileImageId);
        }
        userRepository.delete(userId);
    }

    @Override
    public boolean existsById(UUID userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
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