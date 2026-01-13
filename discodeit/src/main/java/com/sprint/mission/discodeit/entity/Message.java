package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private final UUID id;
    private final UUID channelId;
    private final UUID senderId;
    private String content;
    private final long createdAt;
    private long updatedAt;

    public Message(UUID channelId, UUID senderId, String content) {
        this.id = UUID.randomUUID();
        this.channelId = channelId;
        this.senderId = senderId;
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
    public UUID getSenderId() {
        return senderId;
    }
    public String getContent() {
        return content;
    }
    public long getCreatedAt() {
        return createdAt;
    }
    public long getUpdatedAt() {
        return updatedAt;
    }

    public void updateContent(String content) {
        this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }

}
