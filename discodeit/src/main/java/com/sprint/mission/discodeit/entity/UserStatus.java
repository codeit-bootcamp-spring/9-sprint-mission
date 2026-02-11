package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Getter
public class UserStatus {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private UUID userId;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastSeenAt;

    public UserStatus(UUID userId, Instant lastSeenAt) {
        this.userId = userId;
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.lastSeenAt = lastSeenAt;
    }

    public boolean isOnline() {
        Instant now = Instant.now();
        return lastSeenAt.isAfter(now.minus(5, ChronoUnit.MINUTES));
    }

    public void updateLastSeenAt() {
        Instant now = Instant.now();
        this.lastSeenAt = now;
        this.updatedAt = now;
    }
}
