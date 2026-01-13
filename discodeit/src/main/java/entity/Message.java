package entity;

import java.util.UUID;

public class Message {
    private UUID id;
    private UUID channelId;
    private UUID senderId;
    private String content;
    private Long createdAt;
    private Long updatedAt;

    public Message(UUID channelId, UUID senderId, String content) {
        this.id = UUID.randomUUID();
        this.channelId = channelId;
        this.senderId = senderId;
        this.content = content;
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
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

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void update(String content) {
        this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }
}
