package com.sprint.mission.discodeit.entity.status;

import com.sprint.mission.discodeit.DTO.UserDTO;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID userId;
    private final Long createdAt;
    private Long lastUpdatedAt;


    public UserStatus(UUID userId) {
        this.userId = userId;
        this.createdAt = Instant.now().toEpochMilli();
        this.lastUpdatedAt = this.createdAt;

    }


//사용자 별 마지막으로 확인된 접속 시간을 표현하는 도메인 모델
//    public void lastLogIn(){
//        this.lastUpdatedAt = Instant.now().toEpochMilli();
//    }


//마지막 접속 시간을 기준으로 현재 로그인한 유저로 판단할 수 있는 메소드
    public boolean isOnline(){
        long now = Instant.now().toEpochMilli();
        long fiveMinutes = 5 * 60 * 1000L;
        //5분, 60초, 1000ms
        return now - lastUpdatedAt <= fiveMinutes;
    }



}
