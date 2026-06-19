package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.sse.SseMessage;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Repository;

@Repository
public class SseMessageRepository {

  private static final int MAX_RETAINED_EVENT_COUNT = 1000;

  private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
  private final ConcurrentMap<UUID, SseMessage> messages = new ConcurrentHashMap<>();

  public SseMessage save(Collection<UUID> receiverIds, String eventName, Object data) {
    UUID eventId = UUID.randomUUID();
    SseMessage message = new SseMessage(
        eventId,
        toReceiverIdSet(receiverIds),
        eventName,
        data,
        Instant.now()
    );
    messages.put(eventId, message);
    eventIdQueue.addLast(eventId);
    trim();
    return message;
  }

  public List<SseMessage> findAllAfter(UUID lastEventId, UUID receiverId) {
    if (lastEventId == null) {
      return List.of();
    }
    boolean include = !messages.containsKey(lastEventId);
    List<SseMessage> result = new ArrayList<>();
    for (UUID currentEventId : eventIdQueue) {
      if (currentEventId.equals(lastEventId)) {
        include = true;
        continue;
      }
      if (!include) {
        continue;
      }
      SseMessage message = messages.get(currentEventId);
      if (message != null && message.isReceivableBy(receiverId)) {
        result.add(message);
      }
    }
    return result;
  }

  private Set<UUID> toReceiverIdSet(Collection<UUID> receiverIds) {
    if (receiverIds == null || receiverIds.isEmpty()) {
      return Set.of();
    }
    return Set.copyOf(new LinkedHashSet<>(receiverIds));
  }

  private void trim() {
    while (eventIdQueue.size() > MAX_RETAINED_EVENT_COUNT) {
      UUID expiredEventId = eventIdQueue.pollFirst();
      if (expiredEventId == null) {
        return;
      }
      messages.remove(expiredEventId);
    }
  }
}
