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

  public void save(UUID userId, SseEmitter emitter) {
    data.computeIfAbsent(userId, key -> new CopyOnWriteArrayList<>()).add(emitter);
  }

  public List<SseEmitter> findByUserId(UUID userId) {
    return data.getOrDefault(userId, List.of());
  }

  public ConcurrentMap<UUID, List<SseEmitter>> findAll() {
    return data;
  }

  public void remove(UUID userId, SseEmitter emitter) {
    List<SseEmitter> emitters = data.get(userId);
    if (emitters != null) {
      emitters.remove(emitter);
      if (emitters.isEmpty()) {
        data.remove(userId);
      }
    }
  }
}
