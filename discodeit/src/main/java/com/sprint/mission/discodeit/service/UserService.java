package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    // 생성
    User create(String name, String email);
    // 단건 조회
    User findById(UUID id);
    // 전체 조회
    List<User> findAll();
    // 수정
    User update(UUID id, String name, String email);
    // 삭제
    void delete(UUID id);
}
