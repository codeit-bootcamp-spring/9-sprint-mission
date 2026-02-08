package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserProfileDto;
import com.sprint.mission.discodeit.dto.UserResponseDto;
import com.sprint.mission.discodeit.dto.UserUpdateDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.User.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.User.UserStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    /* ======================
       CREATE
     ====================== */
    @Override
    public UserResponse create(
            UserCreateRequest userRequest,
            ProfileImageRequest imageRequest
    ) {
        if (userRepository.existsByUsername(userRequest.username())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(userRequest.email())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User(
                userRequest.username(),
                userRequest.email(),
                userRequest.password()
        );
        userRepository.save(user);

        UserStatus status = new UserStatus(user.getId());
        userStatusRepository.save(status);

//        UUID profileImageId = null;
//        if (imageRequest != null) {
//          
//        }
//
//        return new UserResponse(
//                user.getId(),
//                user.getUsername(),
//                user.getEmail(),
//                status.isOnline(),
//                profileImageId
//        );
//    }

    /* ======================
       FIND
     ====================== */
    @Override
    public UserResponseDto find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        UserStatus status = userStatusRepository.findByUserId(userId)
                .orElseThrow();

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                status.isOnline(),
                user.getProfileImageId()
        );
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserStatus status = userStatusRepository
                            .findByUserId(user.getId())
                            .orElseThrow();

                    return new UserResponse(
                            user.getId(),
                            user.getUsername(),
                            user.getEmail(),
                            status.isOnline(),
                            user.getProfileImageId()
                    );
                })
                .toList();
    }

    /* ======================
       UPDATE
     ====================== */
    @Override
    public UserResponseDto update(
            UserUpdateDto request,
            UserProfileDto imageRequest
    ) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        user.update(
                request.username(),
                request.email(),
                request.password()
        );
        userRepository.save(user);

        if (imageRequest != null) {
            binaryContentRepository.deleteByOwnerId(user.getId());

            BinaryContent newImage = new BinaryContent(
                    imageRequest.fileName(),
                    imageRequest.bytes(),
                    imageRequest.contentType(),
                    user.getId()
            );
            binaryContentRepository.save(newImage);
        }

        UserStatus status = userStatusRepository
                .findByUserId(user.getId())
                .orElseThrow();

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                status.isOnline(),
                user.getProfileImageId()
        );
    }

    /* ======================
       DELETE
     ====================== */
    @Override
    public void delete(UUID userId) {
        userStatusRepository.deleteByUserId(userId);
        binaryContentRepository.deleteByOwnerId(userId);
        userRepository.deleteById(userId);
    }



}
