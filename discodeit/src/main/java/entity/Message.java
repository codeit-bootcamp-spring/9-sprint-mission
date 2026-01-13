package entity;

import java.util.UUID;

public class Message {
    private final UUID id;
    private final User sender;
    private final User receiver;
    private final UUID Roomid;
    private String content;
    private Long createdAt;
    private Long updatedAt;

    public Message(String content, User sender, User receiver,UUID Roomid) {
        this.id = UUID.randomUUID();
        this.sender = sender;
        this.receiver = receiver;
        this.Roomid = Roomid;
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

    public UUID getRoomid() {
        return Roomid;
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
        return String.format("[%s -> %s] %s",
                sender.getUsername(),
                receiver.getUsername(),
                content);
    }

    public void update(String content) {
        this.content = content;
    }
}


