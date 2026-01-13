package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User {
    private final UUID id;
    private String name;
    private String email;
    private final long  createdAt;
    private long  updatedAt;

    public User(String name, String email) {
        this.id =  UUID.randomUUID();
        this.name = name;
        this.email = email;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }

    // getter,setter
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() { return email; }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void updateName(String name) {
        this.name = name;
        this.updatedAt = System.currentTimeMillis();
    }

    public void updateEmail(String email) {
        this.email = email;
        this.updatedAt = System.currentTimeMillis();
    }

}
