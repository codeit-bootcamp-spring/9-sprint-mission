package com.sprint.mission.discodeit.entity;
import lombok.Getter;
import java.io.Serializable;
import java.util.UUID;
@Getter
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

    public void update(String content) {
        this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }
}