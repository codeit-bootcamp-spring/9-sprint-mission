package com.sprint.mission.discodeit.dto;

import java.util.List;
import java.util.UUID;

/**
 * 메시지 생성 요청 DTO
 * - 요구사항: 첨부파일(여러 개) 가능
 */
public record MessageCreateRequest(
        String content,
        UUID channelId,
        UUID authorId,
        List<UUID> attachmentIds // 없으면 null/empty
) {}

