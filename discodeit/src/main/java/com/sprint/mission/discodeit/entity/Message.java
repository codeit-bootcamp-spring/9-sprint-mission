package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID messageId;
    private final Long createdAt;
    private Long updatedAt;
    private String messageText;
    private final UUID channelId;
    private final UUID userId;

    private final UUID authorId;
    private List<UUID> attachmentIds;


    public Message(UUID userId, UUID channelId, String messageText, List<UUID> attachmentIds) {
        this.messageId = UUID.randomUUID();
        this.userId = userId;
        this.channelId = channelId;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.messageText = messageText;
        this.authorId = this.userId;
        this.attachmentIds = attachmentIds;
    }



    public LocalDateTime getCreatedAtLocalDateTime(){
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(this.createdAt)
                , java.time.ZoneId.systemDefault());
    }

    public LocalDateTime getUpdatedAtLocalDateTime() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(this.updatedAt)
                , java.time.ZoneId.systemDefault());
    }

    public Instant getCreatedAtInstant(){
        return Instant.ofEpochMilli(this.createdAt);
    }

    public Instant getUpdatedAtInstant(){
        return Instant.ofEpochMilli(this.updatedAt);
    }


    public void updateMessage(String messageText){
        this.messageText = messageText;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", messagetext='" + messageText + '\'' +
                ", channelId=" + channelId +
                ", userId=" + userId +
                '}';
    }



}




