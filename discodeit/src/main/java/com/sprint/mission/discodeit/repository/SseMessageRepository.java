package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.sse.SseMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.springframework.stereotype.Repository;

@Repository
public class SseMessageRepository {

  private final ConcurrentLinkedDeque<UUID> eventIdQueue =
      new ConcurrentLinkedDeque<>();

  private final Map<UUID, SseMessage> messages =
      new ConcurrentHashMap<>();

  public void save(SseMessage message) {

    eventIdQueue.addLast(message.eventId());

    messages.put(
        message.eventId(),
        message
    );
  }

  public List<SseMessage> findAfter(UUID lastEventId) {

    List<SseMessage> result = new ArrayList<>();

    boolean found = false;

    for (UUID eventId : eventIdQueue) {

      if (!found) {

        if (eventId.equals(lastEventId)) {
          found = true;
        }

        continue;
      }

      SseMessage message = messages.get(eventId);

      if (message != null) {
        result.add(message);
      }
    }

    return result;
  }
}