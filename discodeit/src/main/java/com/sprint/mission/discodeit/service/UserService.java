package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    // 1. 생성 (Create)
    User createUser(User user);

    // 2. 단건 조회 (Read One)
    User getUser(UUID id);

    // 3. 전체 조회 (Read All)
    List<User> getAllUsers();

    // 4. 수정 (Update) - ID와 수정할 정보를 담은 객체를 받음
    User updateUser(UUID id, User user);

    // 5. 삭제 (Delete)
    void deleteUser(UUID id);
}
