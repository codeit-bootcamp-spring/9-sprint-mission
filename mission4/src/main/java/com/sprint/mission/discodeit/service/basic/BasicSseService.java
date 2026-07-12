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

  // 기존에는 60초로 설정되어 있어서, 클라이언트가 30분마다 오는 서버 ping을 받기도 전에
  // 매 60초마다 타임아웃→재연결을 반복했습니다. 재연결 순간(연결이 빠져 있는 그 짧은 틈)에
  // 다른 사용자가 보낸 SSE 이벤트(channels.created/notifications.created 등)가 도착하면
  // 그 이벤트는 유실됩니다. 채널을 막 만들고 바로 메시지를 보내는 상황처럼 이벤트가
  // 촘촘하게 연달아 발생할 때 특히 이 유실 확률이 높아집니다. 타임아웃을 없애 연결을
  // 오래 유지하고, 끊긴 연결은 기존 cleanUp()의 30분 주기 ping이 감지해 정리하도록 합니다.
  public SseEmitter connect(UUID receiverId, UUID lastEventId) {
    SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
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
