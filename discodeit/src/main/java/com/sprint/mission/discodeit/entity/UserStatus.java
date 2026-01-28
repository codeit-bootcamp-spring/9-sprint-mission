package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.UUID;

/**
 * 사용자의 마지막 접속 시간(활동 시간)을 표현
 * - lastActiveAt 기준으로 온라인 여부 판단 메서드 포함
 */
@Getter
public class UserStatus extends BaseEntity {

    private final UUID userId;
    private long lastActiveAt;

    public UserStatus(UUID userId, long lastActiveAt) {
        super();
        this.userId = userId;
        this.lastActiveAt = lastActiveAt;
    }

    public UserStatus(UUID userId) {
        this(userId, System.currentTimeMillis());
    }

    /**
     * 마지막 접속 시간이 현재로부터 5분 이내면 온라인으로 간주
     */
    public boolean isOnline() {
        long now = System.currentTimeMillis();
        long fiveMinutes = 5L * 60 * 1000;
        return (now - lastActiveAt) <= fiveMinutes;
    }

    public void updateLastActiveAt(long lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
        updateTimestamp();
    }

    public void touch() {
        updateLastActiveAt(System.currentTimeMillis());
    }
}

