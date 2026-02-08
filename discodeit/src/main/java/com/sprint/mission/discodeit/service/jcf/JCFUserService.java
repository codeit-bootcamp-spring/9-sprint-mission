package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.DTO.UserDTO;
import com.sprint.mission.discodeit.DTO.UserServiceResponseDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.Exception.UserCrudException;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class JCFUserService implements UserService {
    private final List<User> data = new ArrayList<>();


    //생성 오버라이드
//    @Override
//    public User createUser(String userName, String email, String password, String profileImage) {
//        User user = new User(userName, email, password, profileImage);
//        data.add(user);
//        return user;
//    }

    @Override
    public User createUser(UserDTO.CreateUserDTO dto) {
        User user = new User(dto.userName(), dto.email(), dto.password(), dto.profileImage());
        data.add(user);
        return user;
    }

    @Override
    public UserServiceResponseDTO.FindUserId findByUserId(UserDTO.FindUserDTO dto) {
        return data.stream()
                .filter(user -> user.getId().equals(dto.id()))
                .findFirst()
                .map(user -> new UserServiceResponseDTO.FindUserId(
                        user.getId(),
                        user.isOnline(),
                        user.getUpdatedAt()

                )).orElseThrow(() -> new RuntimeException());
    }

    //조회 오버라이드
//    @Override
//    public User findByUserId(UUID id) {
//        return data.stream()
//                .filter(user -> user.getId().equals(id))
//                .findFirst()
//                .orElseThrow(RuntimeException::new);
//    }

    // 전체조회 오버라이드
    @Override
    public List<UserDTO.FindUserDTO> findAllUser() {
        if (data.isEmpty()) {
           System.out.println("유저가 없습니다.");
        }

        return data.stream().map( user ->
                new UserDTO.FindUserDTO(user.getId(), user.isOnline(), user.getUpdatedAt())
        ).toList();
    }

    @Override
    public User updateUser(UserDTO.updateUserDTO dto) {

        try {
            User user = this.data.stream().filter
                    (u -> u.getUserId().equals(dto.id())).findFirst().orElse(null);

            if (user == null) {
                throw new RuntimeException("해당 Id를 가진 유저가 없습니다.");
            }

            boolean updated = false;

            if (dto.userName() != null) {
                user.updateUserName(dto.userName());
                updated = true;
            }

            if (dto.email() != null) {
                user.updateEmail(dto.email());
                updated = true;
            }

            if (dto.password() != null) {
                user.updatePassword(dto.password());
                updated = true;
            }

            if (!updated) {
                throw new RuntimeException("수정할 값이 없습니다.");
            }

            return user;
        } catch (UserCrudException e) {
            System.out.println("User에서 다음과 같은 에러가 발생했습니다: " + e.getMessage());
            throw e;
        }

    }

    @Override
    public boolean deleteUser(UserDTO.deleteDTO dto) {
        User foundUser = data.stream()
                .filter(user -> user.getId().equals(dto.id()))
                .findFirst().orElseThrow(() -> new RuntimeException("삭제할 유저가 없습니다."));

        data.remove(foundUser);
        return true;
    }
}