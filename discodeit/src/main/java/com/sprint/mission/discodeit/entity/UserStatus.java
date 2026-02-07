package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private UUID userId;
    private final Instant createdAt;
    private Instant lastJoinAt;

    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.createdAt = Instant.now();
        this.lastJoinAt = createdAt;
    }

    public void update() {
        this.lastJoinAt = Instant.now();
    }

    public boolean isOnline() {
        Instant now = Instant.now();
        return this.lastJoinAt.isAfter(Instant.now().minus(5, ChronoUnit.MINUTES));
    }
}
