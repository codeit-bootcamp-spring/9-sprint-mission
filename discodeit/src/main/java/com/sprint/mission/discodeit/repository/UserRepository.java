package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserRepository {

    // 새로 생성되거나 수정된 user를 저장함
    void save(User user);

    boolean remove(UUID id);

    User findByID(UUID id);

    User findByUserName(String userName);

    List<User> findAll();

    boolean registUser(User user);

    boolean withdrawUser(User user);
}
