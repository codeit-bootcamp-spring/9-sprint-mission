package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.SseMessage;
import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.SseMessageRepository;
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

  private static final Long DEFAULT_TIMEOUT = 1000L * 60 * 30; // 30분
  private final SseEmitterRepository sseEmitterRepository;
  private final SseMessageRepository sseMessageRepository;

  public SseEmitter connect(UUID receiverId, UUID lastEventId) {
    SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

    // Emitter 라이프사이클 관리 콜백 등록
    emitter.onCompletion(() -> {
      log.info("SSE connection completed for user: {}", receiverId);
      sseEmitterRepository.delete(receiverId, emitter);
    });
    emitter.onTimeout(() -> {
      log.info("SSE connection timeout for user: {}", receiverId);
      sseEmitterRepository.delete(receiverId, emitter);
    });
    emitter.onError((e) -> {
      log.error("SSE connection error for user: {}", receiverId, e);
      sseEmitterRepository.delete(receiverId, emitter);
    });

    sseEmitterRepository.save(receiverId, emitter);

    // 최초 연결 시 503 방지 더미 이벤트 전송
    ping(emitter);

    // 유실된 메시지 복원 전송
    if (lastEventId != null) {
      List<SseMessage> lostMessages = sseMessageRepository.findMessagesAfter(lastEventId);
      for (SseMessage message : lostMessages) {
        if (message.receiverId() == null || message.receiverId().equals(receiverId)) {
          try {
            emitter.send(SseEmitter.event()
                .id(message.id().toString())
                .name(message.eventName())
                .data(message.data()));
          } catch (IOException e) {
            log.warn("Failed to send lost message to user: {}, messageId: {}", receiverId, message.id());
            sseEmitterRepository.delete(receiverId, emitter);
            break;
          }
        }
      }
    }

    log.info("Successfully established SSE connection for user: {}", receiverId);
    return emitter;
  }

  public void send(Collection<UUID> receiverIds, String eventName, Object data) {
    UUID eventId = UUID.randomUUID();

    for (UUID receiverId : receiverIds) {
      SseMessage message = new SseMessage(eventId, receiverId, eventName, data);
      sseMessageRepository.save(message);

      List<SseEmitter> emitters = sseEmitterRepository.findByReceiverId(receiverId);
      for (SseEmitter emitter : emitters) {
        try {
          emitter.send(SseEmitter.event()
              .id(eventId.toString())
              .name(eventName)
              .data(data));
        } catch (IOException e) {
          log.warn("Failed to send event to user: {}, eventId: {}", receiverId, eventId);
          sseEmitterRepository.delete(receiverId, emitter);
        }
      }
    }
  }

  public void broadcast(String eventName, Object data) {
    UUID eventId = UUID.randomUUID();
    SseMessage message = new SseMessage(eventId, null, eventName, data);
    sseMessageRepository.save(message);

    sseEmitterRepository.findAll().forEach((receiverId, emitters) -> {
      for (SseEmitter emitter : emitters) {
        try {
          emitter.send(SseEmitter.event()
              .id(eventId.toString())
              .name(eventName)
              .data(data));
        } catch (IOException e) {
          log.warn("Failed to broadcast event to user: {}, eventId: {}", receiverId, eventId);
          sseEmitterRepository.delete(receiverId, emitter);
        }
      }
    });
  }

  @Scheduled(fixedDelay = 1000 * 60 * 30) // 30분 주기
  public void cleanUp() {
    log.info("Starting cleanup of expired SSE emitters...");
    sseEmitterRepository.findAll().forEach((receiverId, emitters) -> {
      for (SseEmitter emitter : emitters) {
        if (!ping(emitter)) {
          log.info("Removing inactive emitter for user: {}", receiverId);
          sseEmitterRepository.delete(receiverId, emitter);
        }
      }
    });
    log.info("Cleanup of SSE emitters finished.");
  }

  private boolean ping(SseEmitter sseEmitter) {
    try {
      sseEmitter.send(SseEmitter.event()
          .name("ping")
          .data("ping"));
      return true;
    } catch (IOException e) {
      return false;
    }
  }
}
