package com.sprint.mission.mission2.repository;

import com.sprint.mission.mission2.entity.Message;
import com.sprint.mission.mission2.entity.User;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface UserRepository {
    User read(UUID id);
    List<User> readAll();
    void save(User user);
    void remove(UUID id);
}
