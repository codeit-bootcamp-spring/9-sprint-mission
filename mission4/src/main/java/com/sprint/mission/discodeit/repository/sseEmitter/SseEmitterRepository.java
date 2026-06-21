package com.sprint.mission.discodeit.repository.sseEmitter;

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

  public void save(UUID receiverId, SseEmitter sseEmitter) {
    data.computeIfAbsent(receiverId, k -> new CopyOnWriteArrayList<>()).add(sseEmitter);
  }

  public List<SseEmitter> findAll(UUID receiverId) {
    return data.getOrDefault(receiverId, List.of());
  }

  public List<SseEmitter> findAll() {
    return data.values().stream().flatMap(List::stream).toList();
  }

  public void remove(UUID receiverId, SseEmitter sseEmitter) {
    List<SseEmitter> emitters = data.get(receiverId);
    if (emitters != null) {
      emitters.remove(sseEmitter);
      if (emitters.isEmpty()) {
        data.remove(receiverId);
      }
    }

  }

  public void removeByEmitter(SseEmitter sseEmitter) {
    for (UUID receiverId : data.keySet()) {
      List<SseEmitter> emitters = data.get(receiverId);
      if (emitters.contains(sseEmitter)) {
        emitters.remove(sseEmitter);
        if (emitters.isEmpty()) {
          data.remove(receiverId);
        }
        break;
      }
    }
  }

}
