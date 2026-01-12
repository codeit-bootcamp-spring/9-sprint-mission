package entity;

import java.util.UUID;

public class Message {
    private final UUID id;
    private final Long createdAt;
    private final Long updateAt;

    public Message() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updateAt = System.currentTimeMillis();
    }
}
