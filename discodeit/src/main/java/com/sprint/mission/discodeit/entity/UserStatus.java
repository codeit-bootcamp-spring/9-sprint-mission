package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import java.io.Serial;
import java.time.Instant;
import java.util.UUID;

/**
 * 사용자 상태 엔티티 클래스
 * 사용자의 접속 상태 및 마지막 활동 시간을 추적합니다.
 */
@Getter
public class UserStatus extends BaseEntity {
    /** 직렬화 버전 UID */
    @Serial
    private static final long serialVersionUID = 1L;

    /** 사용자 ID */
    private final UUID userId;

    /** 마지막 접속 시간 */
    private Instant lastActiveAt;

    /**
     * 사용자 상태 생성자
     *
     * @param userId 사용자 ID
     * @param lastActiveAt 마지막 접속 시간
     */
    public UserStatus(UUID userId, Instant lastActiveAt) {
        super();
        this.userId = userId;
        this.lastActiveAt = lastActiveAt;
    }

    /**
     * 마지막 활동 시간을 업데이트합니다.
     * null이 아닌 값만 업데이트되며, 실제 변경이 있을 때만 수정일시가 갱신됩니다.
     *
     * @param lastActiveAt 새로운 마지막 활동 시간 (null이면 업데이트하지 않음)
     */
    public void update(Instant lastActiveAt) {
        if (lastActiveAt != null) {
            this.lastActiveAt = lastActiveAt;
            updateTimeStamp();
        }
    }

    /**
     * 현재 온라인 상태인지 확인합니다.
     * 마지막 접속 시간이 현재 시간으로부터 5분 이내이면 온라인으로 간주합니다.
     *
     * @return 온라인 상태 여부 (true: 온라인, false: 오프라인)
     */
    public boolean isOnline() {
        if (lastActiveAt == null) {
            return false;
        }
        Instant fiveMinutesAgo = Instant.now().minusSeconds(5 * 60);
        return lastActiveAt.isAfter(fiveMinutesAgo);
    }
}
