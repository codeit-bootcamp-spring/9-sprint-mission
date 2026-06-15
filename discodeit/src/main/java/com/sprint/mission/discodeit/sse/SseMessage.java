package com.sprint.mission.discodeit.sse;

import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public record SseMessage(
    UUID id,
    Set<UUID> receiverIds,
    String eventName,
    Object data,
    Instant createdAt
) {

  public boolean supports(UUID receiverId) {
    return receiverIds.isEmpty() || receiverIds.contains(receiverId);
  }

  public static SseMessage of(Collection<UUID> receiverIds, String eventName, Object data) {
    return new SseMessage(
        UUID.randomUUID(),
        Set.copyOf(receiverIds),
        eventName,
        data,
        Instant.now()
    );
  }

  public static SseMessage broadcast(String eventName, Object data) {
    return new SseMessage(
        UUID.randomUUID(),
        Set.of(),
        eventName,
        data,
        Instant.now()
    );
  }
}
