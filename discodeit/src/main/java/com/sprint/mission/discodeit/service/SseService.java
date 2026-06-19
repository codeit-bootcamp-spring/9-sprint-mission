package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.SseMessageRepository;
import com.sprint.mission.discodeit.sse.SseEventNames;
import com.sprint.mission.discodeit.sse.SseMessage;
import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RequiredArgsConstructor
@Service
public class SseService {

  private static final long SSE_TIMEOUT_MILLIS = Duration.ofMinutes(30).toMillis();

  private final SseEmitterRepository sseEmitterRepository;
  private final SseMessageRepository sseMessageRepository;

  public SseEmitter connect(UUID receiverId, UUID lastEventId) {
    SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MILLIS);
    sseEmitterRepository.save(receiverId, emitter);

    emitter.onCompletion(() -> sseEmitterRepository.remove(receiverId, emitter));
    emitter.onTimeout(() -> sseEmitterRepository.remove(receiverId, emitter));
    emitter.onError(throwable -> sseEmitterRepository.remove(receiverId, emitter));

    if (!ping(emitter)) {
      sseEmitterRepository.remove(receiverId, emitter);
      return emitter;
    }

    sseMessageRepository.findAllAfter(lastEventId, receiverId)
        .forEach(message -> sendToEmitter(receiverId, emitter, message));
    return emitter;
  }

  public void send(Collection<UUID> receiverIds, String eventName, Object data) {
    if (receiverIds == null || receiverIds.isEmpty()) {
      return;
    }
    List<UUID> uniqueReceiverIds = List.copyOf(new LinkedHashSet<>(receiverIds));
    SseMessage message = sseMessageRepository.save(uniqueReceiverIds, eventName, data);
    uniqueReceiverIds.forEach(receiverId ->
        sseEmitterRepository.findAllByReceiverId(receiverId)
            .forEach(emitter -> sendToEmitter(receiverId, emitter, message))
    );
  }

  public void broadcast(String eventName, Object data) {
    SseMessage message = sseMessageRepository.save(List.of(), eventName, data);
    sseEmitterRepository.findAll().forEach((receiverId, emitters) ->
        emitters.forEach(emitter -> sendToEmitter(receiverId, emitter, message))
    );
  }

  @Scheduled(fixedDelay = 1000 * 60 * 30)
  public void cleanUp() {
    sseEmitterRepository.findAll().forEach((receiverId, emitters) ->
        emitters.forEach(emitter -> {
          if (!ping(emitter)) {
            sseEmitterRepository.remove(receiverId, emitter);
          }
        })
    );
  }

  private boolean ping(SseEmitter sseEmitter) {
    try {
      sseEmitter.send(SseEmitter.event()
          .name(SseEventNames.PING)
          .data("ok"));
      return true;
    } catch (IOException | IllegalStateException e) {
      log.debug("SSE ping failed. emitter will be removed.", e);
      return false;
    }
  }

  private void sendToEmitter(UUID receiverId, SseEmitter emitter, SseMessage message) {
    try {
      emitter.send(SseEmitter.event()
          .id(message.id().toString())
          .name(message.eventName())
          .data(message.data()));
    } catch (IOException | IllegalStateException e) {
      log.debug("SSE send failed. receiverId={}, eventName={}", receiverId, message.eventName(), e);
      sseEmitterRepository.remove(receiverId, emitter);
    }
  }
}
