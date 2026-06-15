package com.sprint.mission.discodeit.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class SseEmitterRepository {

  private final ConcurrentHashMap<UUID, List<SseEmitter>> data = new ConcurrentHashMap<>();

  public void add(UUID userId, SseEmitter emitter) {
    data.computeIfAbsent(userId, id -> new ArrayList<>()).add(emitter);
  }

  public List<SseEmitter> findByUserId(UUID userId) {
    return data.getOrDefault(userId, List.of());
  }

  public Collection<List<SseEmitter>> findAll() {
    return data.values();
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
