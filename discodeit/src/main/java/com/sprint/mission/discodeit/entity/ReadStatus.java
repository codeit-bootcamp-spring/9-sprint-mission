package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private UUID userId;
    private UUID channelId;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastReadAt;

    public ReadStatus(UUID userId, UUID channelId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.channelId = channelId;
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.lastReadAt = now;
    }

    public void updateLastReadAt() {
        Instant now = Instant.now();
        this.lastReadAt = now;
        this.updatedAt = now;
    }
}
