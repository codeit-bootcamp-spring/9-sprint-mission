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

  private static final int MAX_SIZE = 1000;

  private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
  private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

  public SseMessage save(String eventName, Object data,
      java.util.Collection<UUID> receiverIds) {
    UUID eventId = UUID.randomUUID();
    SseMessage message = new SseMessage(eventId, eventName, data, receiverIds);

    messages.put(eventId, message);
    eventIdQueue.addLast(eventId);

    if (eventIdQueue.size() > MAX_SIZE) {
      UUID oldestId = eventIdQueue.pollFirst();
      if (oldestId != null) {
        messages.remove(oldestId);
      }
    }

    return message;
  }

  /**
   * lastEventId 이후에 발행된 메시지 중,
   * 해당 receiverId에게 전송되어야 할 메시지를 시간 순서대로 반환합니다.
   */
  public List<SseMessage> findAllByReceiverIdAfter(UUID receiverId, UUID lastEventId) {
    List<SseMessage> result = new ArrayList<>();
    boolean found = (lastEventId == null);

    for (UUID eventId : eventIdQueue) {
      if (!found) {
        if (eventId.equals(lastEventId)) {
          found = true;
        }
        continue;
      }

      SseMessage message = messages.get(eventId);
      if (message != null && message.receiverIds().contains(receiverId)) {
        result.add(message);
      }
    }

    return result;
  }
}
