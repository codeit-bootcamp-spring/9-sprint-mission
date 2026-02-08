// entity/UserStatus.java
package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Duration;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final UUID userId;
    private Instant lastSeenAt;
    private final Instant createdAt;
    private Instant updatedAt;

    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        Instant now = Instant.now();
        this.lastSeenAt = now;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public void touch() {
        Instant now = Instant.now();
        this.lastSeenAt = now;
        this.updatedAt = now;
    }

    public boolean isUserOnline(Instant lastAccessTime) {
        if (lastAccessTime == null) return false;

        Duration duration = Duration.between(lastAccessTime, Instant.now());
        return duration.toMinutes() <= 5;
    }
}
