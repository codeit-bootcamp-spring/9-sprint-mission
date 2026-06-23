package com.sprint.mission.discodeit.dto.data;

import java.util.UUID;

public record SseMessage(
    UUID eventId,
    String eventName,
    Object data
) {

}
