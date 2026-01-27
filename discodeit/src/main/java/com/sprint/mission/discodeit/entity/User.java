package com.sprint.mission.discodeit.entity;
import lombok.Getter;
import java.io.Serializable;
import java.util.UUID;
@Getter
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String displayName;
    private String email;
    private String phoneNumber;
    private Long createdAt;
    private Long updatedAt;

    public User(String displayName, String email, String phoneNumber) {
        this.id = UUID.randomUUID();
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
        this.displayName = displayName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public void update(String displayName, String email, String phoneNumber) {
        this.displayName = displayName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.updatedAt = System.currentTimeMillis();
    }
}