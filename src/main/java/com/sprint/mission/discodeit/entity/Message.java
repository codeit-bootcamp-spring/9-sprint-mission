package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Message implements Serializable {
    private final UUID id;
    private String content;
    private final UUID userId;
    private final UUID channelId;
    private final List<UUID> attachmentIds;
    private final Instant createdAt;
    private Instant updatedAt;

    public Message(String content, UUID userId, UUID channelId, List<UUID> attachmentIds) {
        this.id = UUID.randomUUID();
        this.content = content;
        this.userId = userId;
        this.channelId = channelId;
        this.attachmentIds = attachmentIds == null ? new ArrayList<>() : new ArrayList<>(attachmentIds);
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void update(String content) {
        if (content != null) {
            this.content = content;
        }
        this.updatedAt = Instant.now();
    }
}
