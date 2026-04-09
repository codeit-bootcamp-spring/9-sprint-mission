package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserRepository {

    void create(User user);
    User findById(UUID id);
    List<User> findAll();
    boolean update(UUID id, String nickname, String phoneNumber, String password);
    boolean delete(UUID id);
}
