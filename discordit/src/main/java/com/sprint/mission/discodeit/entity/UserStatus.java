package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {
    private final UUID id;
    private final UUID userId;
    private final String userName;
    private final Instant createAt;
    private Instant updateAt;

    public UserStatus(UUID userId, String userName) {
        this.id = UUID.randomUUID();
        Instant now = Instant.now();
        this.userId = userId;
        this.userName = userName;
        updateAt = now;
        createAt = now;
    }

    public void lastAccessTimeUpdater() {
        updateAt = Instant.now();
    }

    public void lastAccessTimeUpdater(Instant time) {
        updateAt = time;
    }

    public String isOnline() {
        try {
            Duration duration = Duration.between(this.getUpdateAt(), Instant.now());
            if(duration.toMinutes() > 5) return "오프라인";
            return "온라인";
        } catch (Exception ignore) {
            return "오프라인";
        }
    }
}
