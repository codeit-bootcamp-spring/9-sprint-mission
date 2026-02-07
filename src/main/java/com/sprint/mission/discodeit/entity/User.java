package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class User implements Serializable {
    private final UUID id;
    private String username;
    private String email;
    private String phone;
    private String password;
    private UUID profileImageId;
    private final Instant createdAt;
    private Instant updatedAt;

    public User(String username, String email, String phone, String password, UUID profileImageId) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.profileImageId = profileImageId;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void update(String username, String email, String phone, String password, UUID profileImageId) {
        if (username != null) {
            this.username = username;
        }
        if (email != null) {
            this.email = email;
        }
        if (phone != null) {
            this.phone = phone;
        }
        if (password != null) {
            this.password = password;
        }
        if (profileImageId != null) {
            this.profileImageId = profileImageId;
        }
        this.updatedAt = Instant.now();
    }
}
