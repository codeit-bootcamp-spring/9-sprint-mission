package com.sprint.mission.discodeit.repository;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusRepository {
    void save(UserStatus userStatus);

    boolean remove(UUID id);

    UserStatus findByID(UUID id);

    UserStatus findByUserID(UUID userId);

    List<UserStatus> findAll();
}
