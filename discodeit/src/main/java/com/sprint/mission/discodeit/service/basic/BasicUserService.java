package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserResponse create(UserCreateRequest request, BinaryContentCreateRequest profileRequest) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("User with username " + request.username() + " already exists");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("User with email " + request.email() + " already exists");
        }

        UUID profileId = null;
        if (profileRequest != null) {
            BinaryContent profile = new BinaryContent(
                    profileRequest.fileName(),
                    profileRequest.contentType(),
                    profileRequest.data()
            );
            profileId = binaryContentRepository.save(profile).getId();
        }

        User user = new User(request.username(), request.email(), request.password(), profileId);
        User savedUser = userRepository.save(user);

        UserStatus userStatus = new UserStatus(savedUser.getId(), Instant.now());
        userStatusRepository.save(userStatus);

        return toUserResponse(savedUser);
    }

    @Override
    public List<UserDto> findAllAsDto() {
        return userRepository.findAll().stream()
                .map(user -> {
                    boolean isOnline = userStatusRepository.findByUserId(user.getId())
                            .map(UserStatus::isOnline)
                            .orElse(false);
                    return new UserDto(
                            user.getId(),
                            user.getCreatedAt(),
                            user.getUpdatedAt(),
                            user.getUsername(),
                            user.getEmail(),
                            user.getProfileId(),
                            isOnline
                    );
                })
                .toList();
    }

    @Override
    public UserResponse update(UUID id, UserUpdateRequest request, BinaryContentCreateRequest profileRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));

        UUID newProfileId = user.getProfileId();
        if (profileRequest != null) {
            if (user.getProfileId() != null) {
                binaryContentRepository.deleteById(user.getProfileId());
            }
            BinaryContent profile = new BinaryContent(
                    profileRequest.fileName(),
                    profileRequest.contentType(),
                    profileRequest.data()
            );
            newProfileId = binaryContentRepository.save(profile).getId();
        }

        user.update(request.newUsername(), request.newEmail(), request.newPassword(), newProfileId);
        User savedUser = userRepository.save(user);

        return toUserResponse(savedUser);
    }

    @Override
    public void delete(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));

        if (user.getProfileId() != null) {
            binaryContentRepository.deleteById(user.getProfileId());
        }
        userStatusRepository.deleteByUserId(id);
        userRepository.deleteById(id);
    }

    /**
     * User 엔티티를 UserResponse DTO로 변환합니다.
     * 사용자 생성/수정 시 응답으로 반환되며, 온라인 상태는 포함하지 않습니다.
     */
    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getProfileId()
        );
    }
}
