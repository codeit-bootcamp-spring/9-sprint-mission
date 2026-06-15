package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.service.SseService;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
public class BasicSseService implements SseService {

  private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60; // 1시간

  // key: receiverId, value: (key: emitterId, value: emitter)
  private final Map<UUID, Map<UUID, SseEmitter>> emitters = new ConcurrentHashMap<>();
  // 재연결 시 누락 이벤트 재전송을 위한 캐시 (key: emitterId, value: 이벤트 목록)
  private final Map<UUID, Map<UUID, Object>> eventCache = new ConcurrentHashMap<>();

  @Override
  public SseEmitter connect(UUID receiverId, UUID lastEventId) {
    UUID emitterId = UUID.randomUUID();
    SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

    emitters
        .computeIfAbsent(receiverId, key -> new ConcurrentHashMap<>())
        .put(emitterId, emitter);

    emitter.onCompletion(() -> remove(receiverId, emitterId));
    emitter.onTimeout(() -> remove(receiverId, emitterId));
    emitter.onError(e -> remove(receiverId, emitterId));

    // 최초 연결 시 더미 이벤트 전송 (연결 확인용)
    try {
      emitter.send(SseEmitter.event()
          .id(emitterId.toString())
          .name("connect")
          .data("connected")
          .reconnectTime(3000));
    } catch (IOException e) {
      log.warn("SSE 연결 더미 이벤트 전송 실패: receiverId={}", receiverId, e);
      remove(receiverId, emitterId);
    }

    // TODO: lastEventId 기반 누락 이벤트 재전송 로직 필요 시 eventCache 활용
    if (lastEventId != null) {
      log.debug("lastEventId={} 이후 누락 이벤트 재전송 요청 (미구현)", lastEventId);
    }

    log.debug("SSE 연결 생성: receiverId={}, emitterId={}", receiverId, emitterId);
    return emitter;
  }

  @Override
  public void send(Collection<UUID> receiverIds, String eventName, Object data) {
    receiverIds.forEach(receiverId -> {
      Map<UUID, SseEmitter> receiverEmitters = emitters.get(receiverId);
      if (receiverEmitters == null) {
        return;
      }

      receiverEmitters.forEach((emitterId, emitter) -> {
        try {
          emitter.send(SseEmitter.event()
              .id(emitterId.toString())
              .name(eventName)
              .data(data, MediaType.APPLICATION_JSON));
        } catch (IOException e) {
          log.debug("SSE 전송 실패, emitter 제거: receiverId={}, emitterId={}", receiverId,
              emitterId, e);
          remove(receiverId, emitterId);
        }
      });
    });
  }

  @Override
  public void broadcast(String eventName, Object data) {
    send(emitters.keySet(), eventName, data);
  }

  @Scheduled(fixedDelay = 1000 * 60 * 30)
  public void cleanUp() {
    log.debug("SSE cleanUp 시작: 대상 receiver 수={}", emitters.size());

    emitters.forEach((receiverId, receiverEmitters) ->
        receiverEmitters.forEach((emitterId, emitter) -> {
          if (!ping(emitter)) {
            remove(receiverId, emitterId);
          }
        })
    );
  }

  private boolean ping(SseEmitter emitter) {
    try {
      emitter.send(SseEmitter.event()
          .name("ping")
          .data("ping"));
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  private void remove(UUID receiverId, UUID emitterId) {
    Map<UUID, SseEmitter> receiverEmitters = emitters.get(receiverId);
    if (receiverEmitters == null) {
      return;
    }

    SseEmitter emitter = receiverEmitters.remove(emitterId);
    if (emitter != null) {
      emitter.complete();
    }

    if (receiverEmitters.isEmpty()) {
      emitters.remove(receiverId);
    }
  }
}
