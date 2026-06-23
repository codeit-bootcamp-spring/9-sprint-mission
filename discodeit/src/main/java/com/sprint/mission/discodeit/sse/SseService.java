package com.sprint.mission.discodeit.sse;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseService {

  private static final long TIMEOUT = 60L * 1000 * 60; // 1시간

  private final SseEmitterRepository sseEmitterRepository;
  private final SseMessageRepository sseMessageRepository;

  public SseEmitter connect(UUID receiverId, UUID lastEventId) {
    SseEmitter sseEmitter = new SseEmitter(TIMEOUT);
    sseEmitterRepository.save(receiverId, sseEmitter);

    sseEmitter.onCompletion(() -> sseEmitterRepository.remove(receiverId, sseEmitter));
    sseEmitter.onTimeout(() -> sseEmitterRepository.remove(receiverId, sseEmitter));
    sseEmitter.onError((e) -> sseEmitterRepository.remove(receiverId, sseEmitter));

    // 최초 연결 시 ping
    if (!ping(sseEmitter)) {
      return sseEmitter;
    }

    // 이전 이벤트 복원
    if (lastEventId != null) {
      List<SseMessage> missedMessages = sseMessageRepository.findAllAfter(lastEventId);
      for (SseMessage message : missedMessages) {
        try {
          sseEmitter.send(SseEmitter.event()
              .id(message.getId().toString())
              .name(message.getEventName())
              .data(message.getData()));
        } catch (IOException e) {
          log.warn("SSE 이벤트 재전송 실패: {}", e.getMessage());
        }
      }
    }

    return sseEmitter;
  }

  public void send(Collection<UUID> receiverIds, String eventName, Object data) {
    UUID eventId = UUID.randomUUID();
    sseMessageRepository.save(new SseMessage(eventId, eventName, data));

    for (UUID receiverId : receiverIds) {
      for (SseEmitter sseEmitter : sseEmitterRepository.findByReceiverId(receiverId)) {
        try {
          sseEmitter.send(SseEmitter.event()
              .id(eventId.toString())
              .name(eventName)
              .data(data));
        } catch (IOException e) {
          sseEmitterRepository.remove(receiverId, sseEmitter);
        }
      }
    }
  }

  public void broadcast(String eventName, Object data) {
    send(sseEmitterRepository.findAllReceiverIds(), eventName, data);
  }

  @Scheduled(fixedDelay = 1000 * 60 * 30)
  public void cleanUp() {
    for (UUID receiverId : sseEmitterRepository.findAllReceiverIds()) {
      for (SseEmitter sseEmitter : sseEmitterRepository.findByReceiverId(receiverId)) {
        if (!ping(sseEmitter)) {
          sseEmitterRepository.remove(receiverId, sseEmitter);
        }
      }
    }
    log.debug("SSE 연결 정리 완료");
  }

  private boolean ping(SseEmitter sseEmitter) {
    try {
      sseEmitter.send(SseEmitter.event().comment("ping"));
      return true;
    } catch (IOException e) {
      return false;
    }
  }
}