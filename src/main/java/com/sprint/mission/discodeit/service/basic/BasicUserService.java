package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public UserResponse create(UserCreateRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists.");
        }
        User user = new User(
                request.getUsername(),
                request.getEmail(),
                request.getPhone(),
                request.getPassword(),
                null
        );
        userRepository.save(user);

        if (request.getProfileImage() != null) {
            UUID profileImageId = saveProfileImage(user.getId(), request.getProfileImage()).getId();
            user.update(null, null, null, null, profileImageId);
        }

        UserStatus userStatus = new UserStatus(user.getId());
        userStatusRepository.save(userStatus);

        return toResponse(user, userStatus);
    }

    @Override
    public UserResponse find(UUID id) {
        User user = userRepository.find(id);
        if (user == null) {
            return null;
        }
        UserStatus userStatus = userStatusRepository.findByUserId(id);
        return toResponse(user, userStatus);
    }

    @Override
    public List<UserResponse> findAll() {
        List<User> users = userRepository.findAll();
        List<UserResponse> responses = new ArrayList<>();
        for (User user : users) {
            UserStatus status = userStatusRepository.findByUserId(user.getId());
            responses.add(toResponse(user, status));
        }
        return responses;
    }

    @Override
    public UserResponse update(UserUpdateRequest request) {
        User user = userRepository.find(request.getId());
        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }

        UUID profileImageId = null;
        if (request.getProfileImage() != null) {
            if (user.getProfileImageId() != null) {
                binaryContentRepository.delete(user.getProfileImageId());
            }
            profileImageId = saveProfileImage(user.getId(), request.getProfileImage()).getId();
        }

        user.update(
                request.getUsername(),
                request.getEmail(),
                request.getPhone(),
                request.getPassword(),
                profileImageId
        );

        UserStatus userStatus = userStatusRepository.findByUserId(user.getId());
        return toResponse(user, userStatus);
    }

    @Override
    public void delete(UUID id) {
        User user = userRepository.find(id);
        if (user == null) {
            return;
        }
        if (user.getProfileImageId() != null) {
            binaryContentRepository.delete(user.getProfileImageId());
        }
        UserStatus userStatus = userStatusRepository.findByUserId(id);
        if (userStatus != null) {
            userStatusRepository.delete(userStatus.getId());
        }
        userRepository.delete(id);
    }

    private BinaryContent saveProfileImage(UUID userId, BinaryContentCreateRequest request) {
        BinaryContent content = new BinaryContent(
                userId,
                null,
                request.getFileName(),
                request.getContentType(),
                request.getData()
        );
        return binaryContentRepository.save(content);
    }

    private UserResponse toResponse(User user, UserStatus userStatus) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setProfileImageId(user.getProfileImageId());
        response.setOnline(userStatus != null && userStatus.isOnline());
        return response;
    }
}
