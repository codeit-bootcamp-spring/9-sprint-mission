package com.sprint.mission.discodeit.entity;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class UserStatus {
    private UUID userId;
    private Instant lastActivedAt;

    public void exitAt(Instant activeAt) {
        if (lastActivedAt == null ||  activeAt.isAfter(lastActivedAt)) {
            this.lastActivedAt = activeAt;
        }
    }
//    minusSeconds는 초 단위로 과거로 이동 시키는 메서드
    public boolean isOnline() {
        return lastActivedAt != null &&
                lastActivedAt.isAfter(Instant.now().minusSeconds(300));
    }
}
