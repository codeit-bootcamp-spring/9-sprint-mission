package com.sprint.mission.discodeit.dto;

import java.util.List;
import java.util.UUID;

/**
 * 메시지 조회 응답 DTO
 */
public record MessageResponse(
        UUID id,
        String content,
        UUID channelId,
        UUID authorId,
        List<UUID> attachmentIds,
        Long createdAt,
        Long updatedAt
) {}

