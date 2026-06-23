package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.data.SseMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.springframework.stereotype.Repository;

// @Repository
public class SseMessageRepository implements SseRepository {

  private static final int MAX_SIZE = 100;  // 최대 저장 개수

  private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
  private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

  public UUID save(String eventName, Object data) {
    UUID eventId = UUID.randomUUID();
    messages.put(eventId, new SseMessage(eventId, eventName, data));
    eventIdQueue.add(eventId);

    // 최대 개수 초과되면 오래된 거 삭제
    if (eventIdQueue.size() > MAX_SIZE) {
      UUID oldest = eventIdQueue.poll();
      messages.remove(oldest);
    }

    return eventId;
  }

  // lastEventId 이후의 메시지들 반환
  public List<SseMessage> findAfter(UUID lastEventId) {
    List<UUID> ids = new ArrayList<>(eventIdQueue);
    int index = ids.indexOf(lastEventId);
    if (index == -1) {
      return List.of();
    }
    return ids.subList(index + 1, ids.size()).stream()
        .map(messages::get)
        .filter(Objects::nonNull)
        .toList();
  }
}
