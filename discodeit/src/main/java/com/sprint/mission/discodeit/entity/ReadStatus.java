package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import java.io.Serial;
import java.time.Instant;
import java.util.UUID;

/**
 * 읽기 상태 엔티티 클래스
 * 사용자가 특정 채널의 메시지를 마지막으로 읽은 시간을 추적합니다.
 */
@Getter
public class ReadStatus extends BaseEntity {
    /** 직렬화 버전 UID */
    @Serial
    private static final long serialVersionUID = 1L;

    /** 사용자 ID */
    private final UUID userId;

    /** 채널 ID */
    private final UUID channelId;

    /** 마지막으로 메시지를 읽은 시간 */
    private Instant lastReadAt;

    /**
     * 읽기 상태 생성자
     *
     * @param userId 사용자 ID
     * @param channelId 채널 ID
     * @param lastReadAt 마지막으로 읽은 시간
     */
    public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
        super();
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
    }

    /**
     * 마지막 읽은 시간을 업데이트합니다.
     * null이 아닌 값만 업데이트되며, 실제 변경이 있을 때만 수정일시가 갱신됩니다.
     *
     * @param lastReadAt 새로운 마지막 읽은 시간 (null이면 업데이트하지 않음)
     */
    public void update(Instant lastReadAt) {
        if (lastReadAt != null && !lastReadAt.equals(this.lastReadAt)) {
            this.lastReadAt = lastReadAt;
            updateTimeStamp();
        }
    }
}