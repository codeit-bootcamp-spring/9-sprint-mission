package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.sse.SseMessage;
import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.SseMessageRepository;
import com.sprint.mission.discodeit.service.SseService;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicSseService implements SseService {

  private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60; // 1시간

  private final SseEmitterRepository sseEmitterRepository;
  private final SseMessageRepository sseMessageRepository;

  @Override
  public SseEmitter connect(UUID receiverId, UUID lastEventId) {
    SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

    sseEmitterRepository.save(receiverId, emitter);

    emitter.onCompletion(() -> sseEmitterRepository.delete(receiverId, emitter));
    emitter.onTimeout(() -> sseEmitterRepository.delete(receiverId, emitter));
    emitter.onError(e -> sseEmitterRepository.delete(receiverId, emitter));

    // 최초 연결 확인용 더미 이벤트
    if (!ping(emitter)) {
      sseEmitterRepository.delete(receiverId, emitter);
      return emitter;
    }

    // lastEventId 이후 유실된 이벤트 재전송
    List<SseMessage> missedMessages = sseMessageRepository.findAllByReceiverIdAfter(receiverId,
        lastEventId);
    for (SseMessage message : missedMessages) {
      try {
        emitter.send(SseEmitter.event()
            .id(message.id().toString())
            .name(message.eventName())
            .data(message.data(), MediaType.APPLICATION_JSON));
      } catch (IOException e) {
        log.debug("누락 이벤트 재전송 실패: receiverId={}, eventId={}", receiverId, message.id(), e);
        sseEmitterRepository.delete(receiverId, emitter);
        break;
      }
    }

    log.debug("SSE 연결 생성: receiverId={}, lastEventId={}", receiverId, lastEventId);
    return emitter;
  }

  @Override
  public void send(Collection<UUID> receiverIds, String eventName, Object data) {
    SseMessage message = sseMessageRepository.save(eventName, data, receiverIds);

    receiverIds.forEach(receiverId -> {
      List<SseEmitter> emitters = sseEmitterRepository.findAllByReceiverId(receiverId);

      emitters.forEach(emitter -> {
        try {
          emitter.send(SseEmitter.event()
              .id(message.id().toString())
              .name(eventName)
              .data(data, MediaType.APPLICATION_JSON));
        } catch (IOException e) {
          log.debug("SSE 전송 실패, emitter 제거: receiverId={}", receiverId, e);
          sseEmitterRepository.delete(receiverId, emitter);
        }
      });
    });
  }

  @Override
  public void broadcast(String eventName, Object data) {
    List<UUID> receiverIds = sseEmitterRepository.findAllReceiverIds();
    send(receiverIds, eventName, data);
  }

  @Scheduled(fixedDelay = 1000 * 60 * 30)
  public void cleanUp() {
    log.debug("SSE cleanUp 시작");

    for (UUID receiverId : sseEmitterRepository.findAllReceiverIds()) {
      for (SseEmitter emitter : sseEmitterRepository.findAllByReceiverId(receiverId)) {
        if (!ping(emitter)) {
          sseEmitterRepository.delete(receiverId, emitter);
        }
      }
    }
  }

  private boolean ping(SseEmitter emitter) {
    try {
      emitter.send(SseEmitter.event()
          .name("ping")
          .data("ping", MediaType.APPLICATION_JSON));
      return true;
    } catch (IOException e) {
      return false;
    }
  }
}
