package com.sprint.mission.discodeit.dto.request;

import java.time.Instant;

/**
 * 사용자 상태 수정 요청 DTO
 * 사용자의 마지막 접속 시간을 갱신할 때 사용합니다.
 *
 * @param newLastActiveAt 갱신할 마지막 접속 시간
 */
public record UserStatusUpdateRequest(
        Instant newLastActiveAt
) {
}
