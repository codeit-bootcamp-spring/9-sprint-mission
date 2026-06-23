package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.data.SseMessage;
import java.util.ArrayList;
import java.util.Collection;
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

    eventIdQueue.add(message.eventId());

    messages.put(
        message.eventId(),
        message
    );

    while (eventIdQueue.size() > 1000) {

      UUID removed =
          eventIdQueue.poll();

      messages.remove(removed);
    }
  }

  public Collection<SseMessage> findAll() {
    return messages.values();
  }

  public List<SseMessage> findAfter(
      UUID eventId
  ) {

    List<SseMessage> result =
        new ArrayList<>();

    boolean found = false;

    for (UUID id : eventIdQueue) {

      if (id.equals(eventId)) {
        found = true;
        continue;
      }

      if (found) {

        SseMessage message =
            messages.get(id);

        if (message != null) {
          result.add(message);
        }
      }
    }

    return result;
  }
}
