package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Getter
public class UserStatus {
    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    private UUID userId;
    private String type;
    private Instant lastActiveAt; //마지막 활동 시간 필드

    public UserStatus(UUID userId, String type) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.lastActiveAt = Instant.now();
        this.userId = userId;
        this.type = type;
    }
    // 생성되자마자 현재 시간으로 초기화하여 활성화된 ONLINE유저로 만들어줌
    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.userId = userId;
        this.type = "OFFLINE";           //유저 상태구별
    }
    //유저 id만 받고 type은 자동으로 OPPLINE으로 고정해서 생성. 유저가 처음 가입하고 별다른 설정없이 기본적으로 오프라인 상태로 시작할때
    public void update(String type) {
        this.type = type;
        this.updatedAt = Instant.now();
        this.lastActiveAt = Instant.now();    // 마지막 활동 시간을 즉시 최신화
    }

    public boolean isOnline() {
        return this.lastActiveAt != null &&
                this.lastActiveAt.isAfter(Instant.now().minus(5, ChronoUnit.MINUTES));
    }
}
//마지막 활동 시간이 5분보다 이후인가? 지났으면 자동으로 false(오프라인) true(온라인)