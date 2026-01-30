package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus {

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;

    private final UUID userId;
    private Instant lastActiveAt;

    public UserStatus(UUID userId, Instant lastActiveAt) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.userId = userId;
        this.lastActiveAt = lastActiveAt;
        this.updatedAt = this.createdAt;
    }

    public void updateLastActiveAt(Instant time) {
        this.lastActiveAt = time;
        this.updatedAt = Instant.now();
    }

    /**
     * 마지막 접속이 5분 이내면 온라인
     */
    public boolean isOnline() {
        return Duration.between(lastActiveAt, Instant.now()).toMinutes() < 5;
    }
}