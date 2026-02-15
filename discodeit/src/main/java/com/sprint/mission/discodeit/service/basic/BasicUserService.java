package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Service;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public User create(CreateUserRequest request, UUID profileImageId) {
        User newUser = new User(request.name()
                , request.password()
                , request.email()
        );

        boolean registResult = userRepository.registUser(newUser);
        if (!registResult){
            throw new IllegalStateException("유저 생성 실패 (이름/이메일 중복) | 유저 이름: " + request.name() + " | email: " + request.email());
        };

        UserStatus newUserStatus = new UserStatus(newUser.getId());
        newUser.updateProfileImageId(profileImageId);
        newUser.updateUserStateId(newUserStatus.getId());

        userStatusRepository.save(newUserStatus);

        userRepository.save(newUser);

        return newUser;
    }

    @Override
    public void remove(UUID id) {
        User removeUser = userRepository.findByID(id).orElseThrow();
        UserStatus userStatus = userStatusRepository.findByID(removeUser.getUserStateId()).orElseThrow();
        try {
            userStatusRepository.remove(userStatus.getId());
            binaryContentRepository.remove(removeUser.getProfileId());
            userRepository.withdrawUser(removeUser);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserResponse findByID(UUID id) {
        User user = userRepository.findByID(id).orElseThrow();
        return this.convertToUserResponse(user);
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(this::convertToUserResponse)
                .toList();
    }

    @Override
    public User update(UpdateUserRequest request) {
        UUID targetId = request.userId();
        User target = userRepository.findByID(targetId).orElseThrow();
        target.update(request.newName()
                , request.newEmail()
                , request.newPassword());
        try {
            // 프로필 이미지 교체
            if (request.newProfileImageData() != null) {
                binaryContentRepository.remove(target.getProfileId());
                BinaryContent newProfileImage = new BinaryContent(
                        request.newProfileImageName(),
                        "image",
                        request.newProfileImageData()
                );
                binaryContentRepository.save(newProfileImage);
                target.updateProfileImageId(newProfileImage.getId());
            }
            userRepository.save(target);
            return target;
        }catch (Exception e){
            throw new IllegalStateException("User Update 실패 | ID: " + targetId);
        }
    }

    @Override
    public User updateName(UUID id, String newName) {
        User target = userRepository.findByID(id).orElseThrow();
        target.updateName(newName);
        userRepository.save(target);
        return target;
    }

    @Override
    public User updatePassword(UUID id, String newPassword) {
        User target = userRepository.findByID(id).orElseThrow();
        target.updatePassword(newPassword);
        userRepository.save(target);
        return target;
    }

    @Override
    public User updateEmail(UUID id, String newEmail) {
        User target = userRepository.findByID(id).orElseThrow();
        target.updateEmail(newEmail);
        userRepository.save(target);
        return target;
    }

    private UserResponse convertToUserResponse(User user){
        UserStatus userStatus = userStatusRepository.findByID(user.getUserStateId()).orElseThrow();

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getProfileId(),
                userStatus.getLastActiveAt(),
                userStatus.checkIsLogin()
        );
    }
}