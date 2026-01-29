package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final UUID userId;
    private Instant lastLoginTime;

    public UserStatus(UUID userId){
        this.id = UUID.randomUUID();
        this.userId = userId;
    }

    public void refreshLoginTime(){
        this.lastLoginTime = Instant.ofEpochSecond(System.currentTimeMillis());
    }

    public boolean checkIsLogin(){
        Instant now = Instant.ofEpochSecond(System.currentTimeMillis());
        return Duration.between(now, this.lastLoginTime).toMinutes() <= 5;
    }
}
