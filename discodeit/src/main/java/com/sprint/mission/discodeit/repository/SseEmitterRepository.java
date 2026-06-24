package com.sprint.mission.discodeit.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class SseEmitterRepository {

  private final ConcurrentMap<UUID, List<SseEmitter>> data = new ConcurrentHashMap<>();

  public SseEmitter save(UUID userId, SseEmitter emitter) {
    data.computeIfAbsent(userId, id -> new ArrayList<>()).add(emitter);
    return emitter;
  }

  public List<SseEmitter> findAllByUserId(UUID userId) {
    return data.getOrDefault(userId, List.of());
  }

  public Map<UUID, List<SseEmitter>> findAll() {
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
