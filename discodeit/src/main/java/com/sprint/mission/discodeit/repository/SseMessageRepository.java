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

@Repository
public class SseMessageRepository {

  private static final int MAX_CACHE_SIZE = 1000;

  private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
  private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

  public void save(SseMessage message) {
    messages.put(message.id(), message);
    eventIdQueue.add(message.id());

    // 슬라이딩 윈도우 방식으로 오래된 캐시 제거
    while (eventIdQueue.size() > MAX_CACHE_SIZE) {
      UUID oldestId = eventIdQueue.poll();
      if (oldestId != null) {
        messages.remove(oldestId);
      }
    }
  }

  public List<SseMessage> findMessagesAfter(UUID lastEventId) {
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
