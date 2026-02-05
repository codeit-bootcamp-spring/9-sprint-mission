package com.sprint.mission.discodeit.dto.message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageView(
        UUID id,
        UUID channelId,
        UUID senderId,
        String content,
        Instant createdAt,
        Instant updatedAt,
        List<UUID> attachmentIds
) {
}