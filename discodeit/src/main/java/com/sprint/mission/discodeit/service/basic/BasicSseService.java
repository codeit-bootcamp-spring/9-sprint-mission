package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.SseMessage;
import com.sprint.mission.discodeit.repository.sse.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.sse.SseMessageRepository;
import com.sprint.mission.discodeit.service.SseService;
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
public class BasicSseService implements SseService {

  private final SseEmitterRepository sseEmitterRepository;
  private final SseMessageRepository sseMessageRepository;

  @Override
  public SseEmitter connect(UUID receiverId, UUID lastEventId) {
    SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
    sseEmitterRepository.save(receiverId, sseEmitter);

    sseEmitter.onCompletion(() -> sseEmitterRepository.delete(receiverId, sseEmitter));
    sseEmitter.onTimeout(() -> sseEmitterRepository.delete(receiverId, sseEmitter));
    sseEmitter.onError((e) -> sseEmitterRepository.delete(receiverId, sseEmitter));

    ping(sseEmitter);

    List<SseMessage> missedMessages = sseMessageRepository.findAllAfter(lastEventId);
    missedMessages.forEach(message -> sendToEmitter(sseEmitter, message));

    return sseEmitter;
  }

  @Override
  public void send(Collection<UUID> receiverIds, String eventName, Object data) {
    receiverIds.forEach(receiverId -> {
      SseMessage message = new SseMessage(UUID.randomUUID(), receiverId, eventName, data);
      sseMessageRepository.save(message);

      sseEmitterRepository.findAllByReceiverId(receiverId)
          .forEach(sseEmitter -> sendToEmitter(sseEmitter, message));
    });
  }

  @Override
  public void broadcast(String eventName, Object data) {
    send(sseEmitterRepository.findAll().keySet(), eventName, data);
  }

  @Scheduled(fixedDelay = 1000 * 60 * 30)
  public void cleanUp() {
    sseEmitterRepository.findAll().forEach((receiverId, emitters) ->
        emitters.forEach(emitter -> {
          if (!ping(emitter)) {
            sseEmitterRepository.delete(receiverId, emitter);
          }
        })
    );
    log.info("SSE 연결 정리 완료");
  }

  private boolean ping(SseEmitter sseEmitter) {
    try {
      sseEmitter.send(SseEmitter.event().name("ping").data(""));
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  private void sendToEmitter(SseEmitter sseEmitter, SseMessage message) {
    try {
      sseEmitter.send(SseEmitter.event()
          .id(message.id().toString())
          .name(message.eventName())
          .data(message.data()));
    } catch (IOException e) {
      sseEmitterRepository.delete(message.receiverId(), sseEmitter);
    }
  }
}