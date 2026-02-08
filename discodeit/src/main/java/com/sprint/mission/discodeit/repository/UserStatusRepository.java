package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.status.UserStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserStatusRepository {
    UserStatus save(UserStatus userStatus);

    Optional<UserStatus> findByUserId(UUID userId);

    Optional<UserStatus> findAllByUserId(UUID userId);

    UserStatus update(UserStatus userStatus);

    UserStatus updateByUserId(UUID userId, UserStatus userStatus);

    boolean existsById(UUID userId);

    void deleteById(UUID userId);

}
