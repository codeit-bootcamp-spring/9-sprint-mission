package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MessageDto {
    // 메시지 생성
    record CreateRequest(
            String content,
            UUID channelId,
            UUID authorId,
            List<UUID> attachmentIds
    ) {}

    // 메시지 응답
    record Response(
            UUID id,
            String content,
            UUID channelId,
            UUID authorId,
            List<UUID> attachmentIds,
            Instant createdAt
    ) {}
}