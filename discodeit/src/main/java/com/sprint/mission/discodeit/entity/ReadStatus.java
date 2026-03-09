package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
public class ReadStatus  {

    @Id
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    //
    private UUID userId;
    private UUID channelId;
    private Instant lastReadAt;

    protected ReadStatus() {}

    public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
    }

    public void update(Instant newLastReadAt) {
        boolean anyValueUpdated = false;
        if (newLastReadAt != null && !newLastReadAt.equals(this.lastReadAt)) {
            this.lastReadAt = newLastReadAt;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }
}
