package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus extends BaseEntity {

    private final UUID userId;
    private boolean online;
    private Instant lastSeenAt;

    public UserStatus(UUID userId, boolean online) {
        super();
        this.userId = userId;
        this.online = online;
        this.lastSeenAt = Instant.now();
    }

    public void setOnline(boolean online) {
        this.online = online;
        this.lastSeenAt = Instant.now();
        updateTimestamp();
    }
}


