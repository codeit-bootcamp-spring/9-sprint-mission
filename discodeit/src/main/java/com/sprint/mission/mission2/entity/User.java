package com.sprint.mission.mission2.entity;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    UUID id;
    Long createdAt;
    Long updatedAt;
    String name;
    String email;
    String phoneNumber;

    public User(UUID userId, String name, String email, String phoneNumber) {
        this.id = userId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
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

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void update(String name, String email, String phoneNumber) {
        this.updatedAt = System.currentTimeMillis();
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}