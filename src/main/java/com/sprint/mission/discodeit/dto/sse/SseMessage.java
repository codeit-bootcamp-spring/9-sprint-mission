package com.sprint.mission.discodeit.dto.sse;

import java.util.Collection;
import java.util.UUID;

public record SseMessage(
    UUID id,
    String eventName,
    Object data,
    Collection<UUID> receiverIds
) {

}
