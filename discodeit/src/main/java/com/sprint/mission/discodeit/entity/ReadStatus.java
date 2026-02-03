package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;

public class ReadStatus {

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
        this.lastReadAt = Instant.now();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void updateLastRead() {
        this.lastReadAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public UUID getChannelId() { return channelId; }
    public Instant getLastReadAt() { return lastReadAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
