package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class Message implements Serializable {
    private final UUID id;
    private final Instant createAt;
    private final UUID channelId;
    private final UUID userId;
    private final List<UUID> attachmentIds;
    private Instant updateAt;
    private String content;
    @Serial
    private static final long serialVersionUID = 1L;

    public Message(UUID channelId, UUID userId, String content, List<UUID> attachmentIds) {
        Instant n = Instant.now();
        this.id = UUID.randomUUID();
        this.channelId = channelId;
        this.userId = userId;
        this.content = content;
        this.createAt = n;
        this.updateAt = n;
        this.attachmentIds = attachmentIds;
    }

    public UUID getSenderUserId() {
        return userId;
    }

    public UUID getSendChannelId() {
        return channelId;
    }

    public void updateMessage(String content) {
        setContent(content);
    }

    private void setContent(String content) {
        this.content = content;
        setUpdateAt();
    }

    private void setUpdateAt() {
        this.updateAt = Instant.now();
    }
}
