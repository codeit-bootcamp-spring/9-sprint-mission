package com.sprint.mission.discodeit.repository;


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
  private final Map<UUID, Object> messages = new ConcurrentHashMap<>();

  public void save(UUID eventId, Object message) {
    eventIdQueue.addLast(eventId);
    messages.put(eventId, message);

    if (eventIdQueue.size() > 1000) {
      UUID oldest = eventIdQueue.pollFirst();
      if (oldest != null) {
        messages.remove(oldest);
      }
    }
  }

  public List<Object> findMessagesAfter(UUID lastEventId) {
    List<Object> result = new ArrayList<>();
    boolean startCollecting = false;

    for (UUID id : eventIdQueue) {
      if (startCollecting) {
        Object msg = messages.get(id);
        if (msg != null) {
          result.add(msg);
        }
      }

      if (id.equals(lastEventId)) {
        startCollecting = true;
      }
    }

    return result;
  }

}
