package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class UserStatus extends BaseEntity {
    private UUID userId;
    private Instant lastAccessedAt;

    public UserStatus(UUID userId) {
        super();
        this.userId = userId;
        this.lastAccessedAt = Instant.now();
    }
    public boolean isOnline() {
        if (this.lastAccessedAt == null) return false;
        return lastAccessedAt.isAfter(Instant.now().minusSeconds(300)); // 300초 = 5분
    }

    public void updateLastAccessedAt() {
        this.lastAccessedAt = Instant.now();
        this.recordUpdate();
    }
}