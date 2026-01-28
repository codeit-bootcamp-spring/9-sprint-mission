package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class UserStatus extends BaseEntity {
    private UUID userId; // 대상 유저 ID

    public UserStatus(UUID userId) {
        super();
        this.userId = userId;
    }

    /**
     * 마지막 접속 시간을 기준으로 현재 온라인인지 판단합니다.
     * 마지막 접속 시간(updatedAt)이 현재로부터 5분 이내이면 접속 중으로 간주합니다.
     */
    public boolean isOnline() {
        if (getUpdatedAt() == null) return false;
        // C++의 std::chrono::duration과 유사한 시간 차이 계산 [cite: 2025-11-18]
        return Duration.between(getUpdatedAt(), Instant.now()).toMinutes() < 5;
    }
}