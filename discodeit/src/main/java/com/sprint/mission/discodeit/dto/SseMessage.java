package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record SseMessage(
    UUID id,
    UUID receiverId,
    String eventName,
    Object data
) {
}