package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface UserRepository {
    User save (User user);

    Optional<User> findByUserId(UUID id);

    Optional<User> findByUserName(String userName);

    List<User> findAllUser();

    boolean existsById(UUID id);

    boolean existsByEmail(String email);

    boolean existsByUserName(String userName);

    boolean existByProfileId(UUID profileId);

    void deleteById(UUID id);
}
