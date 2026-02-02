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

        userStatusRepository.save(newUserStatus);
        binaryContentRepository.save(profileImage);

        newUser.updateProfileImageId(profileImage.getId());

        userRepository.save(newUser);

        return newUser;
    }

    @Override
    public void remove(UUID id) {
        User removeUser = userRepository.findByID(id);
        UserStatus userStatus = userStatusRepository.findByUserID(id);
        BinaryContent binaryContent = binaryContentRepository.findByID(removeUser.getProfileId());
        try {
            userStatusRepository.remove(userStatus.getId());
            binaryContentRepository.remove(removeUser.getProfileId());
            userRepository.remove(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FindUserResponse findByID(UUID id) {
        User user = userRepository.findByID(id);
        UserStatus userStatus = userStatusRepository.findByUserID(id);

        return new FindUserResponse(
                id,
                user.getName(),
                user.getEmail(),
                user.getProfileId(),
                userStatus.getLastLoginTime(),
                userStatus.checkIsLogin()
        );
    }

    @Override
    public List<FindUserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    // 임시 (레포지토리가 없음)
                    UserStatus userStatus = new UserStatus(user.getId());
                    return new FindUserResponse(
                            user.getId(),
                            user.getName(),
                            user.getEmail(),
                            null,
                            userStatus.getLastLoginTime(),
                            userStatus.checkIsLogin()
                    );
                })
                .toList();
    }

    @Override
    public User update(UpdateUserRequest request) {
        UUID targetId = request.userId();
        User target = userRepository.findByID(targetId);
        if (target == null) {
            throw new IllegalStateException("유저 정보 변경 실패 (해당 유저가 존재하지 않음) | 유저ID: " + targetId);
        }
        target.update(request.newName()
                , request.newPassword()
                , request.newEmail());
        try {
            // 프로필 이미지 수정
            if (request.newProfileImageData() != null) {
                BinaryContent profileImage = binaryContentRepository.findByID(target.getProfileId());
                profileImage.updateData(request.newProfileImageData());
            }
            userRepository.save(target);
            return target;
        }catch (Exception e){

            throw e;
        }
    }

    @Override
    public User updateName(UUID id, String newName) {
        User target = userRepository.findByID(id);
        if (target == null) {
            throw new IllegalStateException("유저 이름 변경 실패 (해당 유저가 존재하지 않음) | 유저ID: " + id);
        }
        target.updateName(newName);
        userRepository.save(target);
        return target;
    }

    @Override
    public User updatePassword(UUID id, String newPassword) {
        User target = userRepository.findByID(id);
        if (target == null) {
            throw new IllegalStateException("유저 전화번호 변경 실패 (해당 유저가 존재하지 않음) | 유저ID: " + id);
        }
        target.updatePassword(newPassword);
        userRepository.save(target);
        return target;
    }

    @Override
    public User updateEmail(UUID id, String newEmail) {
        User target = userRepository.findByID(id);
        if (target == null) {
            throw new IllegalStateException("유저 이메일 변경 실패 (해당 유저가 존재하지 않음) | 유저ID: " + id);
        }
        target.updateEmail(newEmail);
        userRepository.save(target);
        return target;
    }
}
