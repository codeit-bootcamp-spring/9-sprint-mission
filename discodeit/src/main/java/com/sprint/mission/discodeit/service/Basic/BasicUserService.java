package com.sprint.mission.discodeit.service.Basic;

import com.sprint.mission.discodeit.DTO.UserService.Request.CreateUserRequest;
import com.sprint.mission.discodeit.DTO.UserService.Response.FindUserResponse;
import com.sprint.mission.discodeit.DTO.UserService.Request.UpdateUserRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentOwnerType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
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
    public User create(CreateUserRequest request) {
        User newUser = new User(request.name()
                , request.password()
                , request.email()
        );

        UserStatus newUserStatus = new UserStatus(newUser.getId());

        BinaryContent profileImage = new BinaryContent(BinaryContentOwnerType.User,
                newUser.getId(),
                request.profileImageData()
        );

        newUser.updateProfileImageId(profileImage.getId());
        newUser.updateUserStateId(newUserStatus.getId());

        if (!userRepository.registUser(newUser)){
            throw new IllegalStateException("유저 생성 실패 (이름/이메일 중복) | 유저 이름: " + request.name() + " | email: " + request.email());
        };

        userStatusRepository.save(newUserStatus);
        binaryContentRepository.save(profileImage);

        return newUser;
    }

    @Override
    public void remove(UUID id) {
        User removeUser = userRepository.findByID(id).orElseThrow();
        UserStatus userStatus = userStatusRepository.findByUserID(id).orElseThrow();
        //BinaryContent binaryContent = binaryContentRepository.findByID(removeUser.getProfileId()).orElseThrow();
        try {
            userStatusRepository.remove(userStatus.getId());
            binaryContentRepository.remove(removeUser.getProfileId());
            userRepository.withdrawUser(removeUser);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FindUserResponse findByID(UUID id) {
        User user = userRepository.findByID(id).orElseThrow();
        return this.convertToFindUserResponse(user);
    }

    @Override
    public List<FindUserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(this::convertToFindUserResponse)
                .toList();
    }

    @Override
    public User update(UpdateUserRequest request) {
        UUID targetId = request.userId();
        User target = userRepository.findByID(targetId).orElseThrow();
        target.update(request.newName()
                , request.newEmail()
                , request.newPassword()
        );
        try {
            // 프로필 이미지 교체
            if (request.newProfileImageData() != null) {
                binaryContentRepository.remove(target.getProfileId());
                BinaryContent newProfileImage = new BinaryContent(BinaryContentOwnerType.User,
                        targetId,
                        request.newProfileImageData()
                );
                binaryContentRepository.save(newProfileImage);
                target.updateProfileImageId(newProfileImage.getId());
            }
            userRepository.save(target);
            return target;
        }catch (Exception e){

            throw e;
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

    private FindUserResponse convertToFindUserResponse(User user){
        UserStatus userStatus = userStatusRepository.findByUserID(user.getId()).orElseThrow();

        return new FindUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getProfileId(),
                userStatus.getLastActiveAt(),
                userStatus.checkIsLogin()
        );
    }
}
