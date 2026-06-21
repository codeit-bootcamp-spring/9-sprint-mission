package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.repository.sseEmitter.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.sseEmitter.SseMessage;
import com.sprint.mission.discodeit.repository.sseEmitter.SseMessageRepository;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@Slf4j
@RequiredArgsConstructor
public class BasicSseService {

  private final SseMessageRepository messageRepository;
  private final SseEmitterRepository emitterRepository;

  public SseEmitter connect(UUID receiverId, UUID lastEventId) {
    SseEmitter sseEmitter = new SseEmitter(60 * 1000L);
    emitterRepository.save(receiverId, sseEmitter);
    sseEmitter.onCompletion(() -> emitterRepository.remove(receiverId, sseEmitter));
    sseEmitter.onTimeout(() -> emitterRepository.remove(receiverId, sseEmitter));

    List<SseMessage> messages = messageRepository.findAfter(lastEventId);
    for (SseMessage message : messages) {
      sendToClient(sseEmitter, message);
    }
    return sseEmitter;
  }

  public void send(Collection<UUID> receiverIds, String eventName, Object data) {
    SseMessage message = new SseMessage(UUID.randomUUID(), eventName, data);
    messageRepository.save(message);
    for (UUID receiverId : receiverIds) {
      List<SseEmitter> emitters = emitterRepository.findAll(receiverId);
      for (SseEmitter emitter : emitters) {
        sendToClient(emitter, message);
      }
    }
  }

  public void broadcast(String eventName, Object data) {
    SseMessage message = new SseMessage(UUID.randomUUID(), eventName, data);
    messageRepository.save(message);

    List<SseEmitter> emitters = emitterRepository.findAll();
    for (SseEmitter emitter : emitters) {
      sendToClient(emitter, message);
    }
  }

  @Scheduled(fixedDelay = 1000 * 60 * 30)
  public void cleanUp() {
    List<SseEmitter> allEmitters = emitterRepository.findAll();
    for (SseEmitter emitter : allEmitters) {
      ping(emitter);
    }
  }


  public boolean ping(SseEmitter sseEmitter) {
    try {
      sseEmitter.send(SseEmitter.event().name("ping").data("ping"));
      return true;
    } catch (Exception e) {
      sseEmitter.completeWithError(e);
      return false;
    }
  }


  private void sendToClient(SseEmitter sseEmitter, SseMessage message) {
    try {
      sseEmitter.send(SseEmitter.event()
          .id(message.id().toString())
          .name(message.name())
          .data(message.data()));
    } catch (Exception e) {
      log.error("SSE 전송 실패: {}", e.getMessage());
      emitterRepository.removeByEmitter(sseEmitter);

    }
  }

}
