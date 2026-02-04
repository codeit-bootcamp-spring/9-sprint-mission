package com.sprint.mission.discodeit.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserStatus {
    private UUID id;
    private UUID userId;
    private Instant lastSeenAt;
    private Instant createdAt;
    private Instant updatedAt;

    public boolean isOnline() {
        return lastSeenAt != null &&
                lastSeenAt.isAfter(Instant.now().minusSeconds(300));
    }

    // [추가됨] 마지막 접속 시간 업데이트
    public void update(Instant lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
        this.updatedAt = Instant.now();
    }
}
