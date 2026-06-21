package com.sprint.mission.discodeit.event;

import java.util.Collection;
import java.util.UUID;

public record SseEvent(
    Collection<UUID> receiverIds, // null 이면 broadcast
    String eventName,
    Object data
) {
}
