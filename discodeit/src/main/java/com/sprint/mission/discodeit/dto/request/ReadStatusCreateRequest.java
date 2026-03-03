package com.sprint.mission.discodeit.dto.request;

import java.time.Instant;
import java.util.UUID;

/**
 * 읽기 상태 생성 요청 DTO
 * 사용자의 채널별 메시지 읽기 상태를 생성할 때 사용합니다.
 *
 * @param userId 사용자 ID
 * @param channelId 채널 ID
 * @param lastReadAt 마지막으로 읽은 시간
 */
public record ReadStatusCreateRequest(
        UUID userId,
        UUID channelId,
        Instant lastReadAt
) {
}
