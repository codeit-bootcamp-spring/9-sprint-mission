package entity;

import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private String userName;
    private String channelId;
    private String content;
    private Long createdAt;
    private Long updatedAt;

    public Message(String userName, String content, String channelId) {
        this.id = UUID.randomUUID();
        this.userName = userName;
        this.content = content;
        this.channelId = channelId;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return userName;
    }

    public String getChannelId() {
        return channelId;
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

    public void setContent(String content) {
        this.content = content;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
