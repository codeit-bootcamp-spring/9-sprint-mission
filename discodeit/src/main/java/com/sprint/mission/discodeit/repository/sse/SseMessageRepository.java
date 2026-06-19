package com.sprint.mission.discodeit.repository.sse;

import com.sprint.mission.discodeit.dto.SseMessage;
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
    messages.put(message.id(), message);
    eventIdQueue.addLast(message.id());

    if (eventIdQueue.size() > MAX_SIZE) {
      UUID oldestId = eventIdQueue.pollFirst();
      messages.remove(oldestId);
    }
  }

  public List<SseMessage> findAllAfter(UUID lastEventId) {
    if (lastEventId == null) {
      return List.of();
    }

    List<UUID> ids = eventIdQueue.stream().toList();
    int index = ids.indexOf(lastEventId);

    if (index == -1) {
      return List.of();
    }

    return ids.subList(index + 1, ids.size())
        .stream()
        .map(messages::get)
        .filter(message -> message != null)
        .toList();
  }
}