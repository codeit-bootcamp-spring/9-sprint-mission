package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
public class Message implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final UUID channelId;
    private final UUID senderId;
    private String content;
    private final List<UUID> attachmentIds;
    private final Instant createdAt;
    private Instant updatedAt;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    public Message(UUID channelId, UUID senderId, String content) {
        this(channelId, senderId, content, null);
    }

    public Message(UUID channelId, UUID senderId, String content, List<UUID> attachmentIds) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메시지는 비어 있을 수 없습니다.");
        }
        this.id = UUID.randomUUID();
        this.channelId = channelId;
        this.senderId = senderId;
        this.content = content;
        this.attachmentIds = attachmentIds != null ? new ArrayList<>(attachmentIds) : new ArrayList<>();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void updateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메시지는 비어 있을 수 없습니다.");
        }
        this.content = content;
        this.updatedAt = Instant.now();
    }

    public void addAttachment(UUID binaryContentId) {
        if (binaryContentId == null) {
            throw new IllegalArgumentException("첨부파일 ID는 null일 수 없습니다.");
        }
        this.attachmentIds.add(binaryContentId);
        this.updatedAt = Instant.now();
    }

    public List<UUID> getAttachmentIds() {
        return Collections.unmodifiableList(attachmentIds);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", channelId=" + channelId +
                ", senderId=" + senderId +
                ", content='" + content + '\'' +
                ", createdAt=" + FORMATTER.format(createdAt) +
                ", updatedAt=" + FORMATTER.format(updatedAt) +
                '}';
    }
}
