package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;

public class UserStatus {
    private final UUID id;
    private final UUID userId;
    private Instant lastLoginAt;

    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.lastLoginAt = Instant.now();
    }

    public boolean isOnline() {
        return Instant.now().minusSeconds(300).isBefore(lastLoginAt);
    }

    public void updateLastLogin() { this.lastLoginAt = Instant.now(); }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public Instant getLastLoginAt() { return lastLoginAt; }
}
