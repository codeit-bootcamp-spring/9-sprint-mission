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

    private final UUID userId;
    private final UUID channelId;

    private Instant lastReadAt;

    private final Instant createdAt;
    private Instant updatedAt;

    public ReadStatus(UUID userId, UUID channelId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.channelId = channelId;

        Instant now = Instant.now();
        this.lastReadAt = now;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public void markAsRead() {
        Instant now = Instant.now();

        if (this.lastReadAt != null && now.isBefore(this.lastReadAt)) {
            return;
        }

        this.lastReadAt = now;
        this.updatedAt = now;
    }

}
