package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    private final UUID userId;
    private final UUID channelId;
    private Instant lastReadAt;

    public ReadStatus(UUID id, UUID userId, UUID channelId, Instant lastReadAt) {
        Instant now = Instant.now();
        this.id = id;
        this.userId = userId;
        this.channelId = channelId;
        this.createdAt = now;
        this.updatedAt = now;
        this.lastReadAt = lastReadAt;
    }

    // 복원용
    public ReadStatus(
            UUID id,
            UUID userId,
            UUID channelId,
            Instant lastReadAt,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void markRead(Instant readAt) {
        this.lastReadAt = readAt;
        this.updatedAt = Instant.now();
    }
}