package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * 사용자가 특정 채널에서 마지막으로 메시지를 읽은 시각
 */
@Getter
public class ReadStatus extends BaseEntity {

    private final UUID userId;
    private final UUID channelId;
    private Instant lastReadAt;

    public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
        super();
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
    }

    public ReadStatus(UUID userId, UUID channelId) {
        this(userId, channelId, Instant.now());
    }

    public void updateLastReadAt(Instant lastReadAt) {
        this.lastReadAt = lastReadAt;
        updateTimestamp();
    }

    public void touch() {
        updateLastReadAt(Instant.now());
    }
}

