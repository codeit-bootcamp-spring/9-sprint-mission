// service/basic/BasicUserService.java
package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ProfileCreateRequest;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    public BasicUserService(UserRepository userRepository,
                            UserStatusRepository userStatusRepository,
                            BinaryContentRepository binaryContentRepository) {
        this.userRepository = userRepository;
        this.userStatusRepository = userStatusRepository;
        this.binaryContentRepository = binaryContentRepository;
    }

    @Override
    public User create(UserCreateRequest request) {
        // 1. 중복 검증
        validateDuplicate(request.username(), request.email());

        // 2. 프로필 이미지 처리 (선택적)
        UUID profileId = Optional.ofNullable(request.profileRequest())
                .map(profileRequest -> {
                    String fileName = profileRequest.fileName();
                    String contentType = profileRequest.contentType();
                    byte[] bytes = profileRequest.bytes();

                    BinaryContent binaryContent = new BinaryContent(
                            fileName,
                            (long) bytes.length,
                            contentType,
                            bytes
                    );

                    return binaryContentRepository.save(binaryContent).getId();
                })
                .orElse(null);

        // 3. User 생성 및 저장
        User user = new User(
                request.username(),
                request.email(),
                request.password(),
                profileId
        );
        User savedUser = userRepository.save(user);

        // 4. UserStatus 생성 및 저장
        UserStatus userStatus = new UserStatus(savedUser.getId());
        UserStatus savedStatus = userStatusRepository.save(userStatus);

        // 5. User에 statusId 설정
        savedUser.setStatusId(savedStatus.getId());
        return userRepository.save(savedUser);
    }

    @Override
    public Optional<UserResponse> getUserById(UUID id) {
        return userRepository.findById(id)
                .map(user -> {
                    UserStatus status = userStatusRepository
                            .findByUserId(user.getId())
                            .orElse(null);
                    return UserResponse.from(user, status);
                });
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserStatus status = userStatusRepository
                            .findByUserId(user.getId())
                            .orElse(null);
                    return UserResponse.from(user, status);
                })
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse updateUser(UserUpdateRequest request) {
        User user = userRepository.findById(request.id())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + request.id()));

        // 프로필 이미지 대체 (선택적)
        UUID newProfileId = user.getProfileId(); // 기존 값 유지

        if (request.profileRequest() != null) {
            // 기존 프로필 이미지 삭제
            if (user.getProfileId() != null) {
                binaryContentRepository.deleteById(user.getProfileId());
            }

            // 새 프로필 이미지 저장
            ProfileCreateRequest profileRequest = request.profileRequest();
            BinaryContent binaryContent = new BinaryContent(
                    profileRequest.fileName(),
                    (long) profileRequest.bytes().length,
                    profileRequest.contentType(),
                    profileRequest.bytes()
            );
            newProfileId = binaryContentRepository.save(binaryContent).getId();
        }

        // User 업데이트
        user.update(
                request.username(),
                request.email(),
                request.password(),
                newProfileId
        );
        User updatedUser = userRepository.save(user);

        // UserStatus 조회
        UserStatus status = userStatusRepository
                .findByUserId(updatedUser.getId())
                .orElse(null);

        return UserResponse.from(updatedUser, status);
    }

    @Override
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        // 1. BinaryContent(프로필 이미지) 삭제
        if (user.getProfileId() != null) {
            binaryContentRepository.deleteById(user.getProfileId());
        }

        // 2. UserStatus 삭제
        userStatusRepository.findByUserId(user.getId())
                .ifPresent(status -> userStatusRepository.deleteById(status.getId()));

        // 3. User 삭제
        userRepository.deleteById(id);
    }

    private void validateDuplicate(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }
    }
}
