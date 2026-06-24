package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.SseMessage;
import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.SseMessageRepository;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseService {

  private final SseEmitterRepository emitterRepository;
  private final SseMessageRepository messageRepository;

  public SseEmitter connect(UUID receiverId, UUID lastEventId) {
    SseEmitter emitter = new SseEmitter(0L);
    emitterRepository.save(receiverId, emitter);

    emitter.onCompletion(() -> emitterRepository.remove(receiverId, emitter));
    emitter.onTimeout(() -> emitterRepository.remove(receiverId, emitter));
    emitter.onError(e -> emitterRepository.remove(receiverId, emitter));

    ping(emitter);

    if (lastEventId != null) {
      messageRepository.findAllAfter(lastEventId, receiverId)
          .forEach(message -> sendToEmitter(emitter, message));
    }

    return emitter;
  }

  public void send(Collection<UUID> receiverIds, String eventName, Object data) {
    SseMessage message = new SseMessage(UUID.randomUUID(), receiverIds, eventName, data,
        Instant.now());
    messageRepository.save(message);

    for (UUID receiverId : receiverIds) {
      for (SseEmitter emitter : emitterRepository.findAllByUserId(receiverId)) {
        sendToEmitter(emitter, message);
      }
    }
  }

  public void broadcast(String eventName, Object data) {
    SseMessage message = new SseMessage(UUID.randomUUID(), null, eventName, data, Instant.now());
    messageRepository.save(message);

    emitterRepository.findAll()
        .forEach((userId, emitters) -> emitters
            .forEach(emitter -> sendToEmitter(emitter, message)));
  }

  @Scheduled(fixedDelay = 1000 * 60 * 30)
  public void cleanUp() {
    log.debug("SSE 연결 정리 시작");
    Map<UUID, List<SseEmitter>> all = emitterRepository.findAll();
    all.forEach((userId, emitters) -> {
      List<SseEmitter> dead = new ArrayList<>();
      for (SseEmitter emitter : emitters) {
        if (!ping(emitter)) {
          dead.add(emitter);
        }
      }
      dead.forEach(emitter -> emitterRepository.remove(userId, emitter));
    });
    log.debug("SSE 연결 정리 완료");
  }

  private boolean ping(SseEmitter emitter) {
    try {
      emitter.send(SseEmitter.event().name("ping").data(""));
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  private void sendToEmitter(SseEmitter emitter, SseMessage message) {
    try {
      SseEventBuilder event = SseEmitter.event()
          .id(message.id().toString())
          .name(message.eventName())
          .data(message.data());
      emitter.send(event);
    } catch (IOException e) {
      log.warn("SSE 메시지 전송 실패: eventName={}", message.eventName(), e);
    }
  }
}
