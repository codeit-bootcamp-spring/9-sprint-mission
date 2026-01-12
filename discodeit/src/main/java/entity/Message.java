package entity;

import java.util.UUID;

public class Message {
    private final UUID id;
    private final User sender;
    private final User receiver;
    private final UUID roomid;
    private String content;
    private Long createdAt;
    private Long updatedAt;

    public Message(String content, User sender, User receiver) {
        this.id = UUID.randomUUID();
        this.sender = sender;
        this.receiver = receiver;
        this.roomid = UUID.randomUUID();
        this.content = content;
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
    }
}


