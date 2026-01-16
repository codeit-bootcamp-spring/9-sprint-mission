package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

public interface UserService {

    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByDisplayName(String displayName);
    List<User> findAll();
    void update(User user);
    boolean delete(UUID id);
}