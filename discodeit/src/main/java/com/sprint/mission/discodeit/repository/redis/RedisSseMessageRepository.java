package com.sprint.mission.discodeit.repository.redis;

import com.sprint.mission.discodeit.dto.data.SseMessage;
import com.sprint.mission.discodeit.repository.SseRepository;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisSseMessageRepository implements SseRepository {

  private static final String SSE_MSG_HASH_KEY = "sse:messages";
  private static final String SSE_EVENT_LIST_KEY = "sse:event_queue";
  private static final int MAX_SIZE = 100;
  private static final Duration TTL = Duration.ofMinutes(10); // 메시지 보관 시간

  private final RedisTemplate<String, Object> redisTemplate;

  public UUID save(String eventName, Object data) {
    UUID eventId = UUID.randomUUID();
    SseMessage sseMessage = new SseMessage(eventId, eventName, data);

    redisTemplate.opsForHash().put(SSE_MSG_HASH_KEY, eventId.toString(), sseMessage);

    redisTemplate.opsForList().rightPush(SSE_EVENT_LIST_KEY, eventId.toString());

    Long currentSize = redisTemplate.opsForList().size(SSE_EVENT_LIST_KEY);
    while (currentSize != null && currentSize > MAX_SIZE) {
      Object oldestIdObj = redisTemplate.opsForList().leftPop(SSE_EVENT_LIST_KEY);
      if (oldestIdObj != null) {
        redisTemplate.opsForHash().delete(SSE_MSG_HASH_KEY, oldestIdObj.toString());
      }
      currentSize = redisTemplate.opsForList().size(SSE_EVENT_LIST_KEY);
    }

    redisTemplate.expire(SSE_MSG_HASH_KEY, TTL);
    redisTemplate.expire(SSE_EVENT_LIST_KEY, TTL);

    return eventId;
  }

  public List<SseMessage> findAfter(UUID lastEventId) {
    List<Object> idObjects = redisTemplate.opsForList().range(SSE_EVENT_LIST_KEY, 0, -1);
    if (idObjects == null || idObjects.isEmpty()) {
      return List.of();
    }

    List<String> ids = idObjects.stream().map(Object::toString).toList();
    int index = ids.indexOf(lastEventId.toString());
    if (index == -1) {
      return List.of();
    }

    List<String> targetIds = ids.subList(index + 1, ids.size());
    if (targetIds.isEmpty()) {
      return List.of();
    }

    List<Object> msgObjects = redisTemplate.opsForHash()
        .multiGet(SSE_MSG_HASH_KEY, new ArrayList<>(targetIds));

    return msgObjects.stream()
        .filter(Objects::nonNull)
        .map(obj -> (SseMessage) obj)
        .toList();
  }
}