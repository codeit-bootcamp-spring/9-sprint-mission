package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus extends BaseEntity {

    private UUID userId;
    private Instant lastActiveAt;

    public UserStatus(UUID userId, Instant lastActiveAt) {
        super();
        this.userId = userId;
        this.lastActiveAt = (lastActiveAt == null) ? Instant.now() : lastActiveAt;
    }

    public void touchActive() {
        this.lastActiveAt = Instant.now();
        touch();
    }

    // "마지막 접속 시간이 현재 시간으로부터 5분 이내면 온라인"
    public boolean isOnline() {
        return lastActiveAt != null && lastActiveAt.isAfter(Instant.now().minusSeconds(300));
    }
}


