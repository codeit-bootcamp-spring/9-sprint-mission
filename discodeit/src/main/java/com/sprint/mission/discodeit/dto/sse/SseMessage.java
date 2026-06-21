package com.sprint.mission.discodeit.dto.sse;

import java.util.UUID;

public record SseMessage(
    UUID eventId,
    String eventName,
    Object data
) {
}