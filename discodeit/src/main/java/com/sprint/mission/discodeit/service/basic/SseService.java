package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.SseRepository;
import java.io.IOException;
import java.util.Collection;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@Slf4j
@RequiredArgsConstructor
public class SseService {

  private final SseEmitterRepository sseEmitterRepository;
  private final SseRepository sseRepository;

  public SseEmitter connect(UUID receiverId, UUID lastEventId) {
    Collection<SseEmitter> oldEmitters = sseEmitterRepository.findByUserId(receiverId);
    if (oldEmitters != null && !oldEmitters.isEmpty()) {
      if (lastEventId == null) {
        log.info("새 로그인으로 인한 유령 SSE 세션 파괴 (유저 ID: {})", receiverId);
      } else {
        log.info("네트워크 단선으로 인한 구형 SSE 세션 교체 (유저 ID: {}, LastEventId: {})", receiverId, lastEventId);
      }

      oldEmitters.forEach(oldEmitter -> {
        try {
          oldEmitter.complete();
        } catch (Exception ignored) {
        }
        sseEmitterRepository.remove(receiverId, oldEmitter);
      });
    }

    SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
    sseEmitterRepository.save(receiverId, emitter);

    emitter.onCompletion(() -> {
      log.info("SSE 연결 완료");
      sseEmitterRepository.remove(receiverId, emitter);
    });

    emitter.onTimeout(() -> {
      log.debug("SSE 연결 타임아웃");
      emitter.complete();
      sseEmitterRepository.remove(receiverId, emitter);
    });

    emitter.onError(exception -> {
      log.debug("SSE 연결 오류: " + exception.getMessage());
      sseEmitterRepository.remove(receiverId, emitter);
    });

    ping(emitter);

    if (lastEventId != null) {
      sseRepository.findAfter(lastEventId).forEach(message -> {
        try {
          emitter.send(SseEmitter.event()
              .id(message.eventId().toString())
              .name(message.eventName())
              .data(message.data()));
        } catch (IOException e) {
          sseEmitterRepository.remove(receiverId, emitter);
        }
      });
    }

    return emitter;
  }

  public void send(Collection<UUID> receiverIds, String eventName, Object data) {
    UUID eventId = sseRepository.save(eventName, data);
    receiverIds.forEach(receiverId ->
        sseEmitterRepository.findByUserId(receiverId).forEach(emitter -> {
          try {
            emitter.send(SseEmitter.event()
                .id(eventId.toString())
                .name(eventName)
                .data(data));
          } catch (IOException e) {
            sseEmitterRepository.remove(receiverId, emitter);
          }
        })
    );
  }

  public void broadcast(String eventName, Object data) {
    send(sseEmitterRepository.findAll().keySet(), eventName, data);
  }

  @Scheduled(fixedDelay = 1000 * 60 * 30)
  public void cleanUp() {
    sseEmitterRepository.findAll().forEach((userId, emitters) ->
        emitters.removeIf(emitter -> !ping(emitter))
    );
  }

  private boolean ping(SseEmitter emitter) {
    try {
      emitter.send(SseEmitter.event().name("ping").data("ping"));
      return true;
    } catch (IOException e) {
      return false;
    }
  }

}
