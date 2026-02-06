package com.sprint.mission.mission2.entity;

import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@Getter
public class Message implements Serializable {
    private final UUID id;
    private UUID channelId;
    private UUID userId;
    private final Long createdAt;
    private Long updatedAt;
    private String content;

    public Message(UUID id, UUID channelId, UUID userId, String content) {
        this.id = id;
        this.channelId = channelId;
        this.userId = userId;
        this.content = content;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
    }

    public void update(String content) {
        this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }
}