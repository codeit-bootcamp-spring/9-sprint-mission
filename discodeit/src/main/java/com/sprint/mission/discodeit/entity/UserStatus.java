package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // 공통
    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;

    // 참조
    private final UUID userId;

    // 의미
    private Instant lastSeenAt;

    private static final Duration ONLINE_THRESHOLD = Duration.ofMinutes(5);

    public UserStatus(UUID id, UUID userId, Instant lastSeenAt) {
        Instant now = Instant.now();
        this.id = id;
        this.userId = userId;
        this.createdAt = now;
        this.updatedAt = now;
        this.lastSeenAt = lastSeenAt;
    }

    // 접속 기록 갱신
    public void touch(Instant seenAt) {
        this.lastSeenAt = seenAt;
        this.updatedAt = Instant.now();
    }

    // 접속여부 확인
    public boolean isOnline(Instant now) {
        if (lastSeenAt == null || now == null) return false;
        if (lastSeenAt.isAfter(now)) return true;

        return Duration.between(lastSeenAt, now)
                .compareTo(ONLINE_THRESHOLD) <= 0;
    }

    /** 편의 메서드: 현재 시각 기준 온라인 여부 */
    public boolean isOnlineNow() {
        return isOnline(Instant.now());
    }
}
