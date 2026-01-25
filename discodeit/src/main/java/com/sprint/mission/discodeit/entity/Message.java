package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String content;
    private UUID userId;
    private UUID channelId;
    private Long createdAt;
    private Long updatedAt;

    public Message(String content, UUID userId, UUID channelId) {
        this.id = UUID.randomUUID();
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
        this.content = content;
        this.userId = userId;
        this.channelId = channelId;
    }

    public UUID getId() { return id; }
    public String getContent() { return content; }
    public UUID getUserId() { return userId; }
    public UUID getChannelId() { return channelId; }
    public Long getCreatedAt() { return createdAt; }
    public Long getUpdatedAt() { return updatedAt; }

    public void update(String content) {
        this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }
}