package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.data.SseMessage;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Repository
public class SseMessageRepository {

  private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
  private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

  private static final int MAX_CACHE_SIZE = 1000;

  public void save(UUID eventId, SseMessage message) {
    messages.put(eventId, message);
    eventIdQueue.addLast(eventId);

    if (eventIdQueue.size() > MAX_CACHE_SIZE) {
      UUID oldEventId = eventIdQueue.pollFirst();
      if (oldEventId != null) {
        messages.remove(oldEventId);
      }
    }
  }

  public List<SseMessage> findAfter(UUID lastEventId) {
    List<SseMessage> missedMessages = new ArrayList<>();
    boolean isAfter = false;

    for (UUID eventId : eventIdQueue) {
      if (isAfter) {
        missedMessages.add(messages.get(eventId));
      }
      if (eventId.equals(lastEventId)) {
        isAfter = true;
      }
    }
    return missedMessages;
  }
}
