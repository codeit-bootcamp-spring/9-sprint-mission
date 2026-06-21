package com.sprint.mission.discodeit.sse;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record SseMessage(
    UUID id,
    Set<UUID> receiverIds,
    String eventName,
    Object data,
    Instant createdAt
) {

  public boolean isBroadcast() {
    return receiverIds == null || receiverIds.isEmpty();
  }

  public boolean isReceivableBy(UUID receiverId) {
    return isBroadcast() || receiverIds.contains(receiverId);
  }
}
