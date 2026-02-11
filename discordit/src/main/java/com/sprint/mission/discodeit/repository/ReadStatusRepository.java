package com.sprint.mission.discodeit.repository;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public interface ReadStatusRepository {
    void create(UUID userId, UUID channelId);

    Instant find(UUID id);

    Map<UUID, Instant> findAllByUserId(UUID userId);

    boolean update(UUID userId, UUID channelId);

    boolean delete(UUID id);

    void deleteForChannel(UUID channelId);

    void deleteForUser(UUID userId);
}
