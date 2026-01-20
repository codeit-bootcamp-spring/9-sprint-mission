package sprintMission2.entity;

import java.util.UUID;

public class User {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String userName;

    public User(String userName) {
        this.id = UUID.randomUUID();
        long currentTime = System.currentTimeMillis();
        this.createdAt = currentTime;
        this.updatedAt = currentTime;
        this.userName = userName;
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

    public String getUserName() {
        return userName;
    }

    public void update(String userName) {
        this.userName = userName;
        this.updatedAt = System.currentTimeMillis();
    }
}
