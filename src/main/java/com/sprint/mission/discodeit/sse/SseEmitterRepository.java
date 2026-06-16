package com.sprint.mission.discodeit.sse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class SseEmitterRepository {

  private final ConcurrentMap<UUID, List<SseEmitter>> data = new ConcurrentHashMap<>();

  public SseEmitter save(UUID userId, SseEmitter emitter) {
    data.computeIfAbsent(userId, k -> Collections.synchronizedList(new ArrayList<>()))
        .add(emitter);

    emitter.onCompletion(() -> remove(userId, emitter));
    emitter.onTimeout(() -> remove(userId, emitter));
    emitter.onError(e -> remove(userId, emitter));

    return emitter;
  }

  public List<SseEmitter> findAllByUserId(UUID userId) {
    return data.getOrDefault(userId, Collections.emptyList());
  }

  public List<SseEmitter> findAll() {
    return data.values().stream()
        .flatMap(List::stream)
        .toList();
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
