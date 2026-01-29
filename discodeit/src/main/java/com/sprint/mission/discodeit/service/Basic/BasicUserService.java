package com.sprint.mission.discodeit.service.Basic;

import com.sprint.mission.discodeit.DTO.UserService.CreateUserRequest;
import com.sprint.mission.discodeit.DTO.UserService.FindUserResponse;
import com.sprint.mission.discodeit.DTO.UserService.UpdateUserRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentOwnerType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
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
    //private final UserStatusRepository userStatusRepository;

    @Override
    public User create(CreateUserRequest createUserRequest) {
        User newUser = new User(createUserRequest.name()
                , createUserRequest.password()
                , createUserRequest.email()
        );
        userRepository.save(newUser);

        // 일단 임시로 생성은 함. 구체적인 기능과 레포지토리 구현체가 없어서 저장을 못함 근데
        UserStatus newUserStatus = new UserStatus(newUser.getId());
        BinaryContent profileImage = new BinaryContent(BinaryContentOwnerType.User,
                newUser.getId(),
                null
                );

        return newUser;
    }

    @Override
    public void remove(UUID id) {
        userRepository.remove(id);
    }

    @Override
    public FindUserResponse findByID(UUID id) {
        User user = userRepository.findByID(id);
        // 임시 (레포지토리가 없음)
        UserStatus userStatus =  new UserStatus(id);

        return new FindUserResponse(
                user.getName(),
                user.getEmail(),
                null,
                userStatus.getLastLoginTime(),
                userStatus.checkIsLogin()
        );
    }

    @Override
    public List<FindUserResponse> getAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    // 임시 (레포지토리가 없음)
                    UserStatus userStatus = new UserStatus(user.getId());
                    return new FindUserResponse(
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
    public User update(UpdateUserRequest updateUserRequest) {
        UUID targetId = updateUserRequest.userId();
        User target = userRepository.findByID(targetId);
        if (target == null) {
            throw new IllegalStateException("유저 정보 변경 실패 (해당 유저가 존재하지 않음) | 유저ID: " + targetId);
        }
        target.update(updateUserRequest.newName()
                , updateUserRequest.newPassword()
                , updateUserRequest.newEmail());

        try {
            // 프로필 이미지 수정

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
