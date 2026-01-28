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

    // 파일 복원용(필요하면 사용)
    public ReadStatus(UUID id, long createdAt, long updatedAt, UUID userId, UUID channelId, Instant lastReadAt) {
        super(id, createdAt, updatedAt);
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
    }

    public void updateLastReadAt(Instant lastReadAt) {
        this.lastReadAt = lastReadAt;
        updateTimestamp();
    }
}

