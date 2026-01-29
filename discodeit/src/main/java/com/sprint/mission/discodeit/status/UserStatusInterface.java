package com.sprint.mission.discodeit.status;

import com.sprint.mission.discodeit.status.time.UserStatus;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusInterface {
    void save(UserStatus userStatus);
    Optional<UserStatus> findByUser(UUID userId);
}
