package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.SseMessage;
import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.SseMessageRepository;
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
  private final SseMessageRepository sseMessageRepository;

  private static final long TIMEOUT = 1000L * 60 * 30;

  public SseEmitter connect(UUID receiverId, UUID lastEventId) {
    SseEmitter sseEmitter = new SseEmitter(TIMEOUT);

    sseEmitter.onCompletion(() -> sseEmitterRepository.remove(receiverId, sseEmitter));
    sseEmitter.onTimeout(() -> sseEmitterRepository.remove(receiverId, sseEmitter));
    sseEmitter.onError(throwable -> sseEmitterRepository.remove(receiverId, sseEmitter));

    sseEmitterRepository.add(receiverId, sseEmitter);

    ping(sseEmitter);

    if (lastEventId != null) {
      sseMessageRepository.findAfter(lastEventId).forEach(sseMessage -> {
        if (sseMessage.getReceiverId() == null || sseMessage.getReceiverId().equals(receiverId)) {
          sendToEmitter(sseEmitter, sseMessage);
        }
      });
    }
    return sseEmitter;
  }

  public void send(Collection<UUID> receiverIds, String eventName, Object data) {
    receiverIds.forEach(receiverId -> {
      SseMessage message = sseMessageRepository.save(
          new SseMessage(eventName, data, receiverId)
      );
      sseEmitterRepository.findByUserId(receiverId)
          .forEach(emitter -> sendToEmitter(emitter, message));
    });

  }

  public void broadcast(String eventName, Object data) {
    SseMessage message = sseMessageRepository.save(
        new SseMessage(eventName, data, null)
    );
    sseEmitterRepository.findAll()
        .forEach(emitters -> emitters.forEach(emitter -> sendToEmitter(emitter, message)));

  }

  @Scheduled(fixedDelay = 1000 * 60 * 60)
  public void cleanUp() {
    log.debug("SSE emitter cleanup");
    sseEmitterRepository.findAll().forEach(emitters
        -> emitters.removeIf(emitter -> !ping(emitter))
    );
  }

  private boolean ping(SseEmitter sseEmitter) {
    try {
      sseEmitter.send(SseEmitter.event().comment("ping"));
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  private void sendToEmitter(SseEmitter sseEmitter, SseMessage sseMessage) {
    try {
      sseEmitter.send(
          SseEmitter.event()
              .id(sseMessage.getId().toString())
              .name(sseMessage.getName())
              .data(sseMessage.getData())
      );
    } catch (IOException e) {
      log.warn("SSE 전송 실패, emitter 제거: {}", e.getMessage());
      sseEmitter.completeWithError(e);
    }
  }

  @Scheduled(fixedDelay = 1000 * 30)
  public void heartbeat() {
    sseEmitterRepository.findAll()
        .forEach(emitters ->
            emitters.removeIf(emitter -> !ping(emitter))
        );
  }

}
