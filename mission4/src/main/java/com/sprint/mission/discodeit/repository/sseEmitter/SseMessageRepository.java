package com.sprint.mission.discodeit.repository.sseEmitter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class SseMessageRepository {

  private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
  private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

  public void save(SseMessage message) {
    messages.put(message.id(), message);
    eventIdQueue.add(message.id());

    if (eventIdQueue.size() > 100) {
      UUID oldId = eventIdQueue.pollFirst();
      if (oldId != null) {
        messages.remove(oldId);
      }
    }
  }

  public List<SseMessage> findAfter(UUID lastEventId) {
    if (lastEventId == null) {
      return Collections.emptyList();
    }
    return eventIdQueue.stream()
        .dropWhile(id -> !id.equals(lastEventId))
        .skip(1)
        .map(messages::get)
        .collect(Collectors.toList());
  }

}
