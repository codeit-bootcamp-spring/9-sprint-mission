package com.sprint.mission.discodeit.sse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.springframework.stereotype.Repository;

@Repository
public class SseMessageRepository {

  private static final int MAX_SIZE = 1000;

  private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
  private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

  public void save(SseMessage message) {
    messages.put(message.getId(), message);
    eventIdQueue.addLast(message.getId());

    while (eventIdQueue.size() > MAX_SIZE) {
      UUID oldestId = eventIdQueue.pollFirst();
      if (oldestId != null) {
        messages.remove(oldestId);
      }
    }
  }

  public List<SseMessage> findAllAfter(UUID lastEventId) {
    List<SseMessage> result = new ArrayList<>();
    boolean found = false;

    for (UUID id : eventIdQueue) {
      if (found) {
        SseMessage message = messages.get(id);
        if (message != null) {
          result.add(message);
        }
      }
      if (id.equals(lastEventId)) {
        found = true;
      }
    }
    return result;
  }
}