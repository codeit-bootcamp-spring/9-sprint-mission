package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final UUID userId;
    private final UUID channelId;
    private Instant updatedAt;
    private Instant createdAt;
    private Instant lastReadAt;

    public ReadStatus(UUID id, UUID userId, UUID channelId) {
        this.id = UUID.randomUUID();
        this.userId = UUID.randomUUID();
        this.channelId = UUID.randomUUID();
        Instant now = Instant.now();
        this.lastReadAt = now;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public void touch() {
        Instant now = Instant.now();
        this.lastReadAt = now;
        this.updatedAt = now;
    }



}
