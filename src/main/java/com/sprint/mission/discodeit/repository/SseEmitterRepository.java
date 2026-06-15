package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class SseEmitterRepository {

  private final ConcurrentMap<UUID, List<SseEmitter>> data = new ConcurrentHashMap<>();

  public void save(UUID receiverId, SseEmitter emitter) {
    data.computeIfAbsent(receiverId, key -> new CopyOnWriteArrayList<>())
        .add(emitter);
  }

  public List<SseEmitter> findAllByReceiverId(UUID receiverId) {
    return data.getOrDefault(receiverId, List.of());
  }

  public List<UUID> findAllReceiverIds() {
    return List.copyOf(data.keySet());
  }

  public void delete(UUID receiverId, SseEmitter emitter) {
    List<SseEmitter> emitters = data.get(receiverId);
    if (emitters == null) {
      return;
    }

    emitters.remove(emitter);

    if (emitters.isEmpty()) {
      data.remove(receiverId);
    }
  }

  public void deleteAllByReceiverId(UUID receiverId) {
    data.remove(receiverId);
  }
}
