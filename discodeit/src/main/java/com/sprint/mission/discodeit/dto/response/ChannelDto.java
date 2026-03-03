package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.ChannelType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * 채널 목록 응답 DTO
 * GET /api/channels 조회 시 반환합니다.
 *
 * @param id 채널 ID
 * @param type 채널 타입 (PUBLIC/PRIVATE)
 * @param name 채널 이름
 * @param description 채널 설명
 * @param participantIds 참여자 ID 목록 (PRIVATE 채널만)
 * @param lastMessageAt 마지막 메시지 시간
 */
public record ChannelDto(
        UUID id,
        ChannelType type,
        String name,
        String description,
        List<UUID> participantIds,
        Instant lastMessageAt
) {
}
