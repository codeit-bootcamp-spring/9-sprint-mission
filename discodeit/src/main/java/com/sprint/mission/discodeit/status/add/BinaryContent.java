package com.sprint.mission.discodeit.status.add;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent {

    private final UUID id;
    private final UUID userId;
    private final UUID messageId;
    private final Instant createdAt;

    public BinaryContent(UUID id, UUID userId, UUID messageId,Instant createdAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.userId = userId;
        this.messageId = messageId;

    }
}
