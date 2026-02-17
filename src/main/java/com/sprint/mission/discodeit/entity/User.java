package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Getter
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private String name;
    private String email;
    private String password;
    private final Instant createdAt;
    private Instant updatedAt;
    private UUID profileId;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneId.systemDefault());

    public User(String name, String email, String password) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일은 필수입니다.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }

        this.id = UUID.randomUUID();
        this.name = name;
        this.email = email;
        this.password = password;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void updateProfile(UUID profileId) {
        this.profileId = profileId;
        this.updatedAt = Instant.now();
    }

    public void update(String name, String email, String password) {
        boolean changed = false;

        if (name != null && !name.equals(this.name)) {
            this.name = name;
            changed = true;
        }
        if (email != null && !email.equals(this.email)) {
            this.email = email;
            changed = true;
        }
        if (password != null && !password.equals(this.password)) {
            this.password = password;
            changed = true;
        }

        if (changed) {
            this.updatedAt = Instant.now();
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", createdAt=" + FORMATTER.format(createdAt) +
                ", updatedAt=" + FORMATTER.format(updatedAt) +
                '}';
    }

}
