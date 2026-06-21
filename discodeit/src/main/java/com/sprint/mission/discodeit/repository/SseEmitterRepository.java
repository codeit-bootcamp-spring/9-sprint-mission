package com.sprint.mission.discodeit.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class SseEmitterRepository {

  private final ConcurrentMap<UUID, List<SseEmitter>> data =
      new ConcurrentHashMap<>();

  public void save(UUID receiverId, SseEmitter emitter) {

    data.compute(receiverId, (id, emitters) -> {

      if (emitters == null) {
        emitters = new ArrayList<>();
      }

      emitters.add(emitter);
      return emitters;
    });
  }

  public List<SseEmitter> findByReceiverId(UUID receiverId) {
    return data.getOrDefault(receiverId, List.of());
  }

  public ConcurrentMap<UUID, List<SseEmitter>> findAll() {
    return data;
  }

  public void remove(UUID receiverId, SseEmitter emitter) {

    List<SseEmitter> emitters = data.get(receiverId);

    if (emitters == null) {
      return;
    }

    emitters.remove(emitter);

    if (emitters.isEmpty()) {
      data.remove(receiverId);
    }
  }
}