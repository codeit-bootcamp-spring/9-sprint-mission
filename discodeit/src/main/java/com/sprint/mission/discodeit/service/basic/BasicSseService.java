package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.SseMessage;
import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.SseMessageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicSseService {

  private final SseEmitterRepository sseEmitterRepository;
  private final SseMessageRepository sseMessageRepository;

  private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

  public SseEmitter connect(UUID receiverId, UUID lastEventId) {
    SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

    emitter.onCompletion(() -> sseEmitterRepository.delete(receiverId, emitter));
    emitter.onTimeout(() -> sseEmitterRepository.delete(receiverId, emitter));
    emitter.onError((e) -> sseEmitterRepository.delete(receiverId, emitter));

    sseEmitterRepository.save(receiverId, emitter);

    ping(emitter);

    if (lastEventId != null) {
      List<SseMessage> missedMessages = sseMessageRepository.findAfter(lastEventId);
      for (SseMessage msg : missedMessages) {
        sendToClient(emitter, msg.id(), msg.name(), msg.data());
      }
    }

    return emitter;
  }

  public void send(Collection<UUID> receiverIds, String eventName, Object data) {
    UUID eventId = UUID.randomUUID();
    SseMessage message = new SseMessage(eventId, eventName, data);

    sseMessageRepository.save(eventId, message);

    for (UUID receiverId : receiverIds) {
      List<SseEmitter> emitters = sseEmitterRepository.findAllByReceiverId(receiverId);
      for (SseEmitter emitter : emitters) {
        if (!sendToClient(emitter, eventId, eventName, data)) {
          sseEmitterRepository.delete(receiverId, emitter);
        }
      }
    }
  }

  public void broadcast(String eventName, Object data) {
    UUID eventId = UUID.randomUUID();
    SseMessage message = new SseMessage(eventId, eventName, data);
    sseMessageRepository.save(eventId, message);

    Map<UUID, List<SseEmitter>> allEmitters = sseEmitterRepository.findAll();
    for (Map.Entry<UUID, List<SseEmitter>> entry : allEmitters.entrySet()) {
      UUID receiverId = entry.getKey();
      for (SseEmitter emitter : entry.getValue()) {
        if (!sendToClient(emitter, eventId, eventName, data)) {
          sseEmitterRepository.delete(receiverId, emitter);
        }
      }
    }
  }

  @Scheduled(fixedDelay = 1000 * 60 * 30)
  public void cleanUp() {
    Map<UUID, List<SseEmitter>> allEmitters = sseEmitterRepository.findAll();
    for (Map.Entry<UUID, List<SseEmitter>> entry : allEmitters.entrySet()) {
      UUID receiverId = entry.getKey();
      for (SseEmitter emitter : entry.getValue()) {
        if (!ping(emitter)) {
          sseEmitterRepository.delete(receiverId, emitter);
        }
      }
    }
  }

  private boolean ping(SseEmitter sseEmitter) {
    return sendToClient(sseEmitter, UUID.randomUUID(), "ping", "keepalive");
  }

  private boolean sendToClient(SseEmitter emitter, UUID eventId, String eventName, Object data) {
    try {
      emitter.send(SseEmitter.event()
          .id(eventId.toString())
          .name(eventName)
          .data(data));
      return true;
    } catch (IOException e) {
      log.warn("SSE 연결이 끊어졌거나 메시지 전송에 실패했습니다.", e);
      return false;
    }
  }
}
