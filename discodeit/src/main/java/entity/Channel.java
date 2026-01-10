package entity;

import java.util.UUID;

public class Channel {

    private UUID id;
    private String name;
    private String description;
    private Long createdAt;
    private Long updatedAt;

    public Channel(String name, String description) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;

        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public void update(String name, String description) {
        this.name = name;
        this.description = description;
        this.updatedAt = System.currentTimeMillis();
    }
}






