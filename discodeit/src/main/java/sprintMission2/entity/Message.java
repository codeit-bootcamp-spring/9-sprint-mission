package sprintMission2.entity;

import java.util.UUID;

public class Message {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String content;
    private User owner;


    public Message(String content, User owner) {
        this.id = UUID.randomUUID();
        long currentTime = System.currentTimeMillis();
        this.createdAt = currentTime;
        this.updatedAt = currentTime;
        this.content = content;
        this.owner = owner;
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public String getContent() {
        return content;
    }

    public User getOwner() {
        return owner;
    }

    public void update(String content) {
        this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }
}
