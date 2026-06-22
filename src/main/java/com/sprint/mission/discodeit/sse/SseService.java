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

  private static final long TIMEOUT = 60 * 60 * 1000L; // 1시간

  private final SseEmitterRepository emitterRepository;
  private final SseMessageRepository messageRepository;

  public SseEmitter connect(UUID receiverId, UUID lastEventId) {
    SseEmitter emitter = new SseEmitter(TIMEOUT);
    emitterRepository.save(receiverId, emitter);

    // 초기 연결 ping
    if (!ping(emitter)) {
      log.warn("SSE 초기 ping 실패: userId={}", receiverId);
      return emitter;
    }

    // 유실 이벤트 복원
    if (lastEventId != null) {
      List<SseMessage> missed = messageRepository.findAfter(lastEventId);
      for (SseMessage msg : missed) {
        sendToEmitter(emitter, msg);
      }
    }

    log.info("SSE 연결: userId={}", receiverId);
    return emitter;
  }

  public void send(Collection<UUID> receiverIds, String eventName, Object data) {
    SseMessage message = messageRepository.save(eventName, data);

    for (UUID receiverId : receiverIds) {
      List<SseEmitter> emitters = emitterRepository.findAllByUserId(receiverId);
      for (SseEmitter emitter : emitters) {
        sendToEmitter(emitter, message);
      }
    }
  }

  public void broadcast(String eventName, Object data) {
    SseMessage message = messageRepository.save(eventName, data);

    for (SseEmitter emitter : emitterRepository.findAll()) {
      sendToEmitter(emitter, message);
    }
  }

  @Scheduled(fixedDelay = 1000 * 60 * 30) // 30분마다
  public void cleanUp() {
    log.debug("SSE cleanUp 실행");
    List<SseEmitter> all = emitterRepository.findAll();
    for (SseEmitter emitter : all) {
      if (!ping(emitter)) {
        emitter.complete(); // onCompletion 콜백으로 자동 제거됨
      }
    }
  }

  private boolean ping(SseEmitter emitter) {
    try {
      emitter.send(SseEmitter.event()
          .name("ping")
          .data(""));
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  private void sendToEmitter(SseEmitter emitter, SseMessage message) {
    try {
      emitter.send(SseEmitter.event()
          .id(message.getId().toString())
          .name(message.getEventName())
          .data(message.getData()));
    } catch (IOException e) {
      log.warn("SSE 전송 실패: eventId={}", message.getId());
      emitter.complete();
    }
  }
}
