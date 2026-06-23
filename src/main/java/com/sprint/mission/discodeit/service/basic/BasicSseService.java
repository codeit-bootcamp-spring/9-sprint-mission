package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.SseMessage;
import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.SseMessageRepository;
import com.sprint.mission.discodeit.service.SseService;
import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
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

  private final SseEmitterRepository emitterRepository;
  private final SseMessageRepository messageRepository;

  private static final long TIMEOUT =
      60L * 60 * 1000;

  @Override
  public SseEmitter connect(
      UUID receiverId,
      UUID lastEventId
  ) {

    SseEmitter emitter =
        new SseEmitter(TIMEOUT);

    emitterRepository.save(
        receiverId,
        emitter
    );

    emitter.onCompletion(() ->
        emitterRepository.remove(
            receiverId,
            emitter
        ));

    emitter.onTimeout(() ->
        emitterRepository.remove(
            receiverId,
            emitter
        ));

    ping(emitter);

    restoreMissedEvents(
        emitter,
        lastEventId
    );

    log.info(
        "SSE 연결 생성 userId={}",
        receiverId
    );

    return emitter;
  }

  @Override
  public void send(
      Collection<UUID> receiverIds,
      String eventName,
      Object data
  ) {

    UUID eventId =
        UUID.randomUUID();

    SseMessage message =
        new SseMessage(
            eventId,
            eventName,
            data,
            Instant.now()
        );

    messageRepository.save(message);

    receiverIds.forEach(receiverId ->

        emitterRepository
            .findByReceiverId(receiverId)
            .forEach(emitter -> {

              try {

                emitter.send(
                    SseEmitter.event()
                        .id(eventId.toString())
                        .name(eventName)
                        .data(data)
                );

              } catch (IOException e) {

                emitter.complete();

                log.warn(
                    "SSE 전송 실패 userId={}",
                    receiverId
                );
              }
            }));
  }

  @Override
  public void broadcast(
      String eventName,
      Object data
  ) {

    send(
        emitterRepository
            .findAll()
            .keySet(),
        eventName,
        data
    );
  }

  @Scheduled(
      fixedDelay = 1000 * 60 * 30
  )
  public void cleanUp() {

    emitterRepository.findAll()
        .values()
        .forEach(list ->
            list.removeIf(
                emitter ->
                    !ping(emitter)
            ));
  }

  private boolean ping(
      SseEmitter emitter
  ) {

    try {

      emitter.send(
          SseEmitter.event()
              .name("ping")
              .data("ping")
      );

      return true;

    } catch (IOException e) {

      emitter.complete();

      return false;
    }
  }

  private void restoreMissedEvents(
      SseEmitter emitter,
      UUID lastEventId
  ) {

    if (lastEventId == null) {
      return;
    }

    messageRepository.findAfter(
        lastEventId
    ).forEach(message -> {

      try {

        emitter.send(
            SseEmitter.event()
                .id(
                    message.eventId()
                        .toString()
                )
                .name(
                    message.eventName()
                )
                .data(
                    message.data()
                )
        );

      } catch (IOException e) {

        emitter.complete();
      }
    });
  }
}