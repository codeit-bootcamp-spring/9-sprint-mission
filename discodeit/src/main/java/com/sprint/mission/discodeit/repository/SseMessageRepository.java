package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.data.SseMessage;
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

  public void save(SseMessage message) {
    eventIdQueue.add(message.id());
    messages.put(message.id(), message);
  }

  public List<SseMessage> findAllAfter(UUID lastEventId, UUID receiverId) {
    boolean found = lastEventId == null;
    List<SseMessage> result = new ArrayList<>();
    for (UUID id : eventIdQueue) {
      if (!found) {
        if (id.equals(lastEventId)) {
          found = true;
        }
        continue;
      }
      SseMessage message = messages.get(id);
      if (message == null) {
        continue;
      }
      if (message.receiverIds() == null || message.receiverIds().contains(receiverId)) {
        result.add(message);
      }
    }
    return result;
  }

  public void deleteById(UUID eventId) {
    eventIdQueue.remove(eventId);
    messages.remove(eventId);
  }
}
