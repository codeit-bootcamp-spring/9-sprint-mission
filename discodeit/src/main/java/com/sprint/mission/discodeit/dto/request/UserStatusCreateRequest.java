package com.sprint.mission.discodeit.dto.request;

import java.time.Instant;
import java.util.UUID;

/**
 * 사용자 상태 생성 요청 DTO
 * 사용자의 온라인 상태를 생성할 때 사용합니다.
 *
 * @param userId 사용자 ID
 * @param lastAccessAt 마지막 접속 시간
 */
public record UserStatusCreateRequest(
        UUID userId,
        Instant lastAccessAt
) {
}
