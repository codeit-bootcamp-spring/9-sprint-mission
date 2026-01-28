// src/main/java/com/sprint/mission/discodeit/repository/UserStatusRepository.java
package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.UUID;

public interface UserStatusRepository {

    void upsert(UserStatus status);

    UserStatus findByUserId(UUID userId);

    void setOnline(UUID userId, boolean online);
}


