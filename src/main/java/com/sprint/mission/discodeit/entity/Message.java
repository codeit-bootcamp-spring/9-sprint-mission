package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Getter
public class Message implements Serializable {
    private final UUID id;
    private final UUID channelId;
    private final UUID senderId;
    private String content;
    private final Long createdAt;
    private Long updatedAt;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Serial
    private static final long serialVersionUID = 1L;

    public Message(UUID channelId, UUID senderId, String content) {

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메시지는 비어 있을 수 없습니다.");
        }

        this.id = UUID.randomUUID();
        this.channelId = channelId;
        this.senderId = senderId;
        this.content = content;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
    }

    public void updateContent(String content) {

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메시지는 비어 있을 수 없습니다.");
        }

        this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", channelId=" + channelId +
                ", senderId=" + senderId +
                ", content='" + content + '\'' +
                ", createdAt=" + sdf.format(new Date(createdAt)) +
                ", updatedAt=" + sdf.format(new Date(updatedAt)) +
                '}';
    }

}
