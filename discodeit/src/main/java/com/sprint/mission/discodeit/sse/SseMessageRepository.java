package com.sprint.mission.discodeit.sse;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.springframework.stereotype.Repository;

@Repository
public class SseMessageRepository {

  private static final int MAX_MESSAGE_COUNT = 1000;

  private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
  private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

  public SseMessage save(SseMessage message) {
    messages.put(message.id(), message);
    eventIdQueue.addLast(message.id());
    trim();
    return message;
  }

  public List<SseMessage> findAllAfter(UUID lastEventId, UUID receiverId) {
    if (lastEventId == null || !messages.containsKey(lastEventId)) {
      return List.of();
    }

    return eventIdQueue.stream()
        .dropWhile(eventId -> !eventId.equals(lastEventId))
        .skip(1)
        .map(messages::get)
        .filter(message -> message != null && message.supports(receiverId))
        .toList();
  }

  public Optional<SseMessage> find(UUID eventId) {
    return Optional.ofNullable(messages.get(eventId));
  }

  private void trim() {
    while (eventIdQueue.size() > MAX_MESSAGE_COUNT) {
      UUID eventId = eventIdQueue.pollFirst();
      if (eventId != null) {
        messages.remove(eventId);
      }
    }
  }
}
