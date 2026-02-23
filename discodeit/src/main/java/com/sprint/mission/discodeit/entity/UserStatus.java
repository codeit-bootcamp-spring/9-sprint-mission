package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@ToString(callSuper = true)
@Getter
public class UserStatus extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID userId;
    private Instant lastActiveAt;

    public UserStatus(UUID userId){
        super();
        this.userId = userId;
        this.lastActiveAt = Instant.MIN;
        System.out.println("UserStatus 생성 - " + this.toString());
    }

    public void updateLastActiveAt(Instant time){
        this.lastActiveAt = time;
    }

    public boolean checkIsLogin(){
        if (this.lastActiveAt  == null){
            return false;
        }
        return lastActiveAt.isAfter(Instant.now().minus(Duration.ofMinutes(5)));
    }
}
