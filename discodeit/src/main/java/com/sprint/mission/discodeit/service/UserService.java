package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.UserDTO;
import com.sprint.mission.discodeit.DTO.UserServiceResponseDTO;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public interface UserService {
    //생성
//    User createUser(String username,String email,String password);
    User createUser(UserDTO.CreateUserDTO dto);
    //조회
//    User findByUserId(UUID id);
    UserServiceResponseDTO.FindUserId findByUserId(UserDTO.FindUserDTO dto);

    //전체 조회
    List<UserDTO.FindUserDTO> findAllUser();

    //수정
    User updateUser(UserDTO.updateUserDTO dto);
    //삭제
    boolean deleteUser(UserDTO.deleteDTO dto);

}
