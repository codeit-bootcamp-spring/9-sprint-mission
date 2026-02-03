package com.sprint.mission.discodeit.status.adds;

import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus {
    private static final Duration ONLINE_THRESHOLD = Duration.ofMinutes(5);
        //ONLINE_THRESHOLD = 온라인 상태의 기준점
    private UUID id;
    private UUID userId;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastView;

    public UserStatus(UUID id, UUID userId, Instant createdAt, Instant updatedAt, Instant lastView) {
        this.id = UUID.randomUUID();
        this.userId = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.lastView = Instant.now();
    }

    public boolean online() {
        Instant onlineLimit = Instant.now().minus(ONLINE_THRESHOLD);
        return lastView.isAfter(onlineLimit);
    }

    public void updateLastView(Instant now) {
        this.lastView = now;
        this.updatedAt = now;
    }

}
