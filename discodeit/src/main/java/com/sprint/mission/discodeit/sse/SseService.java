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

  private static final long TIMEOUT_MILLIS = 1000L * 60 * 60;
  private static final String PING_EVENT_NAME = "ping";

  private final SseEmitterRepository emitterRepository;
  private final SseMessageRepository messageRepository;

  public SseEmitter connect(UUID receiverId, UUID lastEventId) {
    SseEmitter emitter = new SseEmitter(TIMEOUT_MILLIS);
    emitterRepository.save(receiverId, emitter);

    emitter.onCompletion(() -> emitterRepository.delete(receiverId, emitter));
    emitter.onTimeout(() -> emitterRepository.delete(receiverId, emitter));
    emitter.onError(error -> emitterRepository.delete(receiverId, emitter));

    if (!ping(emitter)) {
      emitterRepository.delete(receiverId, emitter);
      return emitter;
    }

    messageRepository.findAllAfter(lastEventId, receiverId)
        .forEach(message -> send(emitter, message));
    return emitter;
  }

  public void send(Collection<UUID> receiverIds, String eventName, Object data) {
    if (receiverIds == null || receiverIds.isEmpty()) {
      return;
    }
    SseMessage message = messageRepository.save(SseMessage.of(receiverIds, eventName, data));
    receiverIds.forEach(receiverId -> emitterRepository.findAllByReceiverId(receiverId)
        .forEach(emitter -> send(emitter, message)));
  }

  public void broadcast(String eventName, Object data) {
    SseMessage message = messageRepository.save(SseMessage.broadcast(eventName, data));
    emitterRepository.findAll()
        .forEach(emitter -> send(emitter, message));
  }

  @Scheduled(fixedDelay = 1000 * 60 * 30)
  public void cleanUp() {
    emitterRepository.findAll().stream()
        .filter(emitter -> !ping(emitter))
        .forEach(emitterRepository::delete);
  }

  private boolean ping(SseEmitter emitter) {
    try {
      emitter.send(SseEmitter.event()
          .name(PING_EVENT_NAME)
          .data("ping"));
      return true;
    } catch (IOException | IllegalStateException e) {
      log.debug("SSE ping failed", e);
      return false;
    }
  }

  private void send(SseEmitter emitter, SseMessage message) {
    try {
      emitter.send(SseEmitter.event()
          .id(message.id().toString())
          .name(message.eventName())
          .data(message.data()));
    } catch (IOException | IllegalStateException e) {
      log.debug("SSE event send failed: eventId={}, eventName={}",
          message.id(), message.eventName(), e);
      emitterRepository.delete(emitter);
    }
  }
}
