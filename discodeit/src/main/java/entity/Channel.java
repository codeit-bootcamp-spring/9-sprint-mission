package entity;

import java.util.UUID;

public class Channel {
    private final UUID ownerid;
    private final UUID id;
    private String name;
    private Long createdAt;
    private Long updatedAt;

    public Channel(String name){
        this.ownerid = UUID.randomUUID();
        this.id = UUID.randomUUID();
        this.name = name;
        long now= System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;

    }

    public UUID getOwnerid() {
        return ownerid;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }
}



