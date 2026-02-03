package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus {
    UUID channelId;
    UUID userId;
    UUID id;
    Long createdAt;
    Long updatedAt;
    Long lastReadAt;

    public ReadStatus(UUID channelId, UUID userId) {
        this.createdAt = Instant.now().getEpochSecond();
        this.lastReadAt = createdAt;
        this.updatedAt = createdAt;
        this.channelId = channelId;
        this.userId = userId;
        this.id = UUID.randomUUID();
    }
}
