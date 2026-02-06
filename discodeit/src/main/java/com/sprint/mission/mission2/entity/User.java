package com.sprint.mission.mission2.entity;

import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@Getter
public class User implements Serializable {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String name;
    private String email;
    private String phoneNumber;

    public User(UUID userId, String name, String email, String phoneNumber) {
        this.id = userId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
    }

    public void update(String name, String email, String phoneNumber) {
        this.updatedAt = System.currentTimeMillis();
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}