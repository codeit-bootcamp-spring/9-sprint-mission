package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus {
    UUID userId;
    UUID id;
    Long createdAt;
    Long updatedAt;
    Long lastJoinAt;

    public UserStatus(UUID userId) {
        this.createdAt = Instant.now().getEpochSecond();
        this.lastJoinAt = createdAt;
        this.updatedAt = createdAt;
        this.userId = userId;
        this.id = UUID.randomUUID();
    }

    public boolean isOnline() {
        return (Instant.now().getEpochSecond() - lastJoinAt) <= 300;
    }

    public void updateLastJoinAt() {
        this.lastJoinAt = Instant.now().getEpochSecond();
        this.updatedAt = lastJoinAt;
    }
}
