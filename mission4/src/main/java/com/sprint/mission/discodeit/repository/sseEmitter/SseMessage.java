package com.sprint.mission.discodeit.repository.sseEmitter;

import java.util.UUID;

public record SseMessage(
    UUID id,
    String name,
    Object data
) {

}
