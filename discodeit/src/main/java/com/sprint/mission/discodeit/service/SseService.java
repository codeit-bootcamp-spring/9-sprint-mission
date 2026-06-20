package com.sprint.mission.discodeit.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class SseService {

  private final Map<UUID, SseEmitter> emitters = new ConcurrentHashMap<>();
  private static final Long DEFAULT_TIMEOUT = 1000L * 60 * 30;

  public SseEmitter connect(UUID receiverId, UUID lastEventId) {
    SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

    sseEmitter.onCompletion(() -> this.emitters.remove(receiverId));
    sseEmitter.onTimeout(() -> this.emitters.remove(receiverId));
    sseEmitter.onError((e) -> this.emitters.remove(receiverId));

    this.emitters.put(receiverId, sseEmitter);

    ping(sseEmitter);

    return sseEmitter;
  }

  public void send(Collection<UUID> receiverIds, String eventName, Object data) {
    for (UUID receiverId : receiverIds) {
      SseEmitter sseEmitter = emitters.get(receiverId);
      if (sseEmitter != null) {
        try {
          sseEmitter.send(SseEmitter.event()
              .name(eventName)
              .data(data));
        } catch (IOException e) {
          this.emitters.remove(receiverId);
        }
      }
    }
  }

  public void broadcast(String eventName, Object data) {
    send(emitters.keySet(), eventName, data);
  }

  @Scheduled(fixedDelay = 1000 * 60 * 30)
  public void cleanUp() {
    for (Map.Entry<UUID, SseEmitter> entry : emitters.entrySet()) {
      UUID receiverId = entry.getKey();
      SseEmitter sseEmitter = entry.getValue();

      if (!ping(sseEmitter)) {
        this.emitters.remove(receiverId);
      }
    }
  }

  private boolean ping(SseEmitter sseEmitter) {
    try {
      sseEmitter.send(SseEmitter.event()
          .name("ping")
          .data("connection-check"));
      return true;
    } catch (IOException e) {
      return false;
    }
  }
}
