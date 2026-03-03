package com.sprint.mission.discodeit.dto.request;

import java.time.Instant;

/**
 * 읽기 상태 수정 요청 DTO
 * 사용자의 마지막 읽은 시간을 갱신할 때 사용합니다.
 *
 * @param newLastReadAt 갱신할 마지막 읽은 시간
 */
public record ReadStatusUpdateRequest(
        Instant newLastReadAt
) {
}
