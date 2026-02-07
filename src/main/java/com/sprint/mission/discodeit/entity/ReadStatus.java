package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus implements Serializable {
    private final UUID id;
    private final UUID userId;
    private final UUID channelId;
    private Instant lastReadAt;
    private final Instant createdAt;
    private Instant updatedAt;

    public ReadStatus(UUID userId, UUID channelId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.channelId = channelId;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.lastReadAt = this.createdAt;
    }

    public void updateLastReadAt(Instant lastReadAt) {
        if (lastReadAt != null) {
            this.lastReadAt = lastReadAt;
        }
        this.updatedAt = Instant.now();
    }
}
