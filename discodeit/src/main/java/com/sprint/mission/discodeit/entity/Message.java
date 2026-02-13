package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class Message implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private UUID channelId;
    private UUID senderId;
    private String content;
    private final Instant createdAt;
    private Instant updatedAt;

    private List<UUID> attachmentIds;

    public Message(UUID channelId, UUID senderId, String content) {
        Instant now = Instant.now();
        this.id = UUID.randomUUID();
        this.channelId = channelId;
        this.senderId = senderId;
        this.content = content;
        this.createdAt = now;
        this.updatedAt = now;
        this.attachmentIds = List.of();
    }

    // 복원용 생성자
    public Message(
            UUID id,
            UUID channelId,
            UUID senderId,
            String content,
            Instant createdAt,
            Instant updatedAt,
            List<UUID> attachmentIds
    ) {
        this.id = id;
        this.channelId = channelId;
        this.senderId = senderId;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.attachmentIds = (attachmentIds == null) ? List.of() : List.copyOf(attachmentIds);
    }

    public void update(String content) {
        this.content = content;
        this.updatedAt = Instant.now();
    }

    public void attach(List<UUID> attachmentIds) {
        this.attachmentIds = (attachmentIds == null) ? List.of() : List.copyOf(attachmentIds);
        this.updatedAt = Instant.now();
    }
}
