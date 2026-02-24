package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@ToString(callSuper = true)
@Getter
public class UserStatus extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private final UUID userId;
    private Instant lastActiveAt;

    public UserStatus(UUID userId){
        super();
        this.userId = userId;
        // 일단 생성 시점부터 활동시작으로
        this.lastActiveAt = Instant.now();
        System.out.println("UserStatus 생성 - " + this.toString());
    }

    public void updateLastActiveAt(Instant time){
        this.lastActiveAt = time;
    }

    public boolean checkIsLogin(){
        return lastActiveAt.isAfter(Instant.now().minus(Duration.ofMinutes(5)));
    }
}
