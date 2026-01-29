package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {
        UserStatus save(UserStatus status);
        Optional<UserStatus> findByUserId(UUID userid);
        List<UserStatus> findAll();
        boolean existUserId(UUID userid);
        void deleteStatus(UUID userid);



}
