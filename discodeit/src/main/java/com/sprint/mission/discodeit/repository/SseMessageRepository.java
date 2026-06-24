package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.SseMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.springframework.stereotype.Repository;

@Repository
public class SseMessageRepository {

  private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
  private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

  private static final int maxQueueSize = 1000;

  public SseMessage save(SseMessage message) {
    if (eventIdQueue.size() >= maxQueueSize) {
      UUID oldestEventId = eventIdQueue.pollFirst();
      messages.remove(oldestEventId);
    }

    eventIdQueue.addLast(message.getId());
    messages.put(message.getId(), message);
    return message;
  }

  public List<SseMessage> findAfter(UUID lastEventId) {
    boolean found = false;
    List<SseMessage> result = new ArrayList<>();
    for (UUID eventId : eventIdQueue) {
      if (found) {
        SseMessage message = messages.get(eventId);
        if (message != null) {
          result.add(message);
        }
      }
      if (eventId.equals(lastEventId)) {
        found = true;
      }
    }
    return result;
  }
}
