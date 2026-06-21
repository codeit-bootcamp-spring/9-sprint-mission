package com.sprint.mission.discodeit.dto.data;

import java.util.UUID;

public record SseMessage(
    UUID id,
    UUID receiverId, // null 이면 broadcast
    String eventName,
    Object data
) {
}
