package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MessageDto {
    record CreateRequest(
            String content,
            UUID authorId,
            UUID channelId,
            List<UUID> attachmentIds
    ) {}

    record Response(
            UUID id,
            String content,
            UUID authorId,
            UUID channelId,
            List<UUID> attachmentIds,
            Instant createdAt,
            Instant updatedAt
    ) {}
}