package com.sprint.mission.discodeit.sse;

import java.util.ArrayList;
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

  public SseMessage save(String eventName, Object data) {
    UUID id = UUID.randomUUID();
    SseMessage message = new SseMessage(id, eventName, data);
    messages.put(id, message);
    eventIdQueue.addLast(id);

    // 최대 크기 초과 시 오래된 것 제거
    while (eventIdQueue.size() > MAX_SIZE) {
      UUID oldest = eventIdQueue.pollFirst();
      if (oldest != null) messages.remove(oldest);
    }

    return message;
  }

  // lastEventId 이후의 메시지들 반환 (유실 복원용)
  public List<SseMessage> findAfter(UUID lastEventId) {
    List<SseMessage> result = new ArrayList<>();
    boolean found = false;

    for (UUID id : eventIdQueue) {
      if (found) {
        SseMessage msg = messages.get(id);
        if (msg != null) result.add(msg);
      }
      if (id.equals(lastEventId)) {
        found = true;
      }
    }

    return result;
  }
}
