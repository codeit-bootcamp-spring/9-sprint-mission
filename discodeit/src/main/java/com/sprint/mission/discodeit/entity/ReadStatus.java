package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.UUID;

/**
 * 사용자가 특정 채널에서 마지막으로 메시지를 읽은 시각을 표현
 * - userId + channelId 조합이 사실상 유니크한 개념
 */
@Getter
public class ReadStatus extends BaseEntity {

    private final UUID userId;
    private final UUID channelId;
    private long lastReadAt;

    public ReadStatus(UUID userId, UUID channelId, long lastReadAt) {
        super();
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
    }

    public ReadStatus(UUID userId, UUID channelId) {
        this(userId, channelId, System.currentTimeMillis());
    }

    public void updateLastReadAt(long lastReadAt) {
        this.lastReadAt = lastReadAt;
        updateTimestamp();
    }

    public void touch() {
        updateLastReadAt(System.currentTimeMillis());
    }
}

