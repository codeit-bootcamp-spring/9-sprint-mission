package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    private final String username;
    private String displayName;
    private String email;
    private String phoneNumber;
    private String password;

    private UUID profileImageId;

    public User(String username, String email, String phoneNumber, String password) {
        Instant now = Instant.now();
        this.id = UUID.randomUUID();
        this.username = username;
        this.displayName = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.createdAt = now;
        this.updatedAt = now;
        this.profileImageId = null;
    }

    public User(String username, String email, String phoneNumber) {
        this(username, email, phoneNumber, null);
    }

    public void update(String displayName, String email, String phoneNumber) {
        if (displayName != null) {
            this.displayName = displayName;
        }
        if (email != null) {
            this.email = email;
        }
        if (phoneNumber != null) {
            this.phoneNumber = phoneNumber;
        }
        this.updatedAt = Instant.now();
    }

    public void changeProfileImage(UUID profileImageId) {
        this.profileImageId = profileImageId;
        this.updatedAt = Instant.now();
    }

    public void changePassword(String password) {
        this.password = password;
        this.updatedAt = Instant.now();
    }
}
