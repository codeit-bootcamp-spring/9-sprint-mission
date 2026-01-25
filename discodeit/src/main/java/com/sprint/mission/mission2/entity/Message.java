package com.sprint.mission.mission2.entity;

import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {
    UUID id;
    UUID channelId;
    UUID userId;
    Long createdAt;
    Long updatedAt;
    String content;

    public Message(UUID id, UUID channelId, UUID userId, String content) {
        this.id = id;
        this.channelId = channelId;
        this.userId = userId;
        this.content = content;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public UUID getUserId() {
        return userId;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public String getContent() {
        return content;
    }

    public void update(String content) {
        this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }
}