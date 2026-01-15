package entity;

import java.util.UUID;

public class Message {
    private final UUID id;
    private User sender;
    private User receiver;
    private String content;
    private final Long createdAt;
    private Long updatedAt;

    public Message(String content, User sender, User receiver) {
        this.id = UUID.randomUUID();
        this.sender = sender;
        this.receiver = receiver;

        this.content = content;
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public UUID getId() {
        return id;
    }

    public User getSender() {
        return sender;
    }

    public User getReceiver() {
        return receiver;
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

    @Override

    public String toString() {
        return String.format("생성시간:%s,업데이트시간:%s,\n[%s -> %s] %s",getCreatedAt(),getUpdatedAt(),
                 sender.getUsername(), receiver.getUsername(), content);
    }

    public void update(String content) {
        this.content = content;
    }
}


