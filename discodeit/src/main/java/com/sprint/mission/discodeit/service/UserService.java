package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    //   유저 생성
    User create(String name, String email, String phoneNumber);
    //   유저 조회 (단건)
    User find(UUID id);
    //   유저 조회 (다건)
    List<User> findAll();
    //   유저 수정
    User update(UUID id, String username, String email, String password);
    //   유저 삭제
    void delete(UUID id);
}
