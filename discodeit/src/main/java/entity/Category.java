package entity;

import java.util.UUID;

public class Category {
    private UUID id;
    private String name;
    private Long createdAt;
    private Long updatedAt;
    //conductor
    public Category(String name) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.name = name;
    }
    //getter
    public UUID getId() { return id; }
    public String getName() { return name; }
    public Long getCreatedAt() { return createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    //update
    public void update(String name) {
        this.name = name;
        this.updatedAt = System.currentTimeMillis();
    }
}