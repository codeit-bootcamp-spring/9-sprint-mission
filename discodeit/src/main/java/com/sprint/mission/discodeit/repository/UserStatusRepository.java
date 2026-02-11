package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.domain.UserStatus;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserStatusRepository {
    UserStatus save(UserStatus userStatus);
    UserStatus findById(UUID id);
    UserStatus findByUserId(UUID userId);
    List<UserStatus> findAll();
    void deleteById(UUID id);
}
