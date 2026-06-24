package com.sprint.mission.discodeit.dto.data;

import java.time.Instant;
import java.util.UUID;

public record SseMessage(
        UUID eventId,
        String eventName,
        Object data,
        Instant createdAt
) {
}
