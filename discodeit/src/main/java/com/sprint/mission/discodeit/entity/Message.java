package main.java.com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.UUID;

public class Message implements Serializable {
    private final UUID id;
    private final UUID senderId;
    private final UUID channelId;
    private final Long createdAt;
    private Long updatedAt;
    private String msgText;
    private String displayName;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Message(String msgText, UUID senderId, UUID channelId) {
        this.id = UUID.randomUUID();
        this.senderId = senderId;
        this.channelId = channelId;
        Long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
        this.msgText = msgText;
    }

    public UUID getId() {
        return id;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public String getMsgText() {
        return msgText;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void update(String updateMsg) {
        this.msgText = updateMsg;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Message{" +
                ", msgText='" + msgText + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}