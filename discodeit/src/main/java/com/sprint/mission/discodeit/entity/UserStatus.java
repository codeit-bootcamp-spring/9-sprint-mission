package com.sprint.mission.discodeit.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class UserStatus {

    private final UUID id;
    private final UUID userId;
    private Instant lastActiveAt;
    private final Instant createdAt;
    private Instant updatedAt;


    public static UserStatus createNew(UUID userId) {
        Instant now = Instant.now();
        return UserStatus.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .lastActiveAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public void updateLastActiveAt() {
        this.lastActiveAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public boolean isOnline() {
        return Duration.between(lastActiveAt, Instant.now()).toMinutes() < 5;
    }
}
