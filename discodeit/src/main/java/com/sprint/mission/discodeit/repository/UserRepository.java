package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    void save(User user);

    boolean remove(UUID id);

    Optional<User> findByID(UUID id);

    Optional<User> findByUserName(String userName);

    List<User> findAll();

    boolean registUser(User user);

    boolean withdrawUser(User user);
}
