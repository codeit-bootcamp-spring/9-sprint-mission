package com.sprint.mission.discodeit.entity;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class ReadStatus {
    private UUID userId;
    private UUID channelId;
    private Instant lastReadAt;

// message가 올라간 시간
    public void updateLastReadAt(Instant readAt) {
        if (readAt.isAfter(this.lastReadAt)) {
            this.lastReadAt = readAt;
        }
    }
// is.After는 두 시간을 비교하는 매서드 (Instant)
// message를 읽은 시간
    public boolean isUnread(Instant messageCreatedAt) {
        return messageCreatedAt.isAfter(this.lastReadAt);
    }
}
