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
        this.lastReadAt = (lastReadAt == null) ? Instant.now() : lastReadAt;
    }

    // 파일 저장소 복원용
    public ReadStatus(UUID id, Instant createdAt, Instant updatedAt,
                      UUID userId, UUID channelId, Instant lastReadAt) {
        super(id, createdAt, updatedAt);
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
    }

    public void updateLastReadAt(Instant at) {
        this.lastReadAt = (at == null) ? Instant.now() : at;
        touch();
    }
}

