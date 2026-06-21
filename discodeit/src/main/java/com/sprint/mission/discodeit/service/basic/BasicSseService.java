package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.repository.SseEmitterRepository;
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
@RequiredArgsConstructor
@Service
public class BasicSseService implements SseService {

  private static final long TIMEOUT = 1000L * 60 * 60;

  private final SseEmitterRepository repository;

  @Override
  public SseEmitter connect(
      UUID receiverId,
      UUID lastEventId
  ) {

    SseEmitter emitter = new SseEmitter(TIMEOUT);

    repository.save(receiverId, emitter);

    emitter.onCompletion(
        () -> repository.remove(receiverId, emitter)
    );

    emitter.onTimeout(
        () -> repository.remove(receiverId, emitter)
    );

    ping(emitter);

    return emitter;
  }

  @Override
  public void send(
      Collection<UUID> receiverIds,
      String eventName,
      Object data
  ) {

    for (UUID receiverId : receiverIds) {

      List<SseEmitter> emitters =
          repository.findByReceiverId(receiverId);

      for (SseEmitter emitter : emitters) {

        try {

          emitter.send(
              SseEmitter.event()
                  .name(eventName)
                  .data(data)
          );

        } catch (IOException e) {

          emitter.complete();
          repository.remove(receiverId, emitter);
        }
      }
    }
  }

  @Override
  public void broadcast(
      String eventName,
      Object data
  ) {

    repository.findAll().forEach(
        (receiverId, emitters) -> {

          for (SseEmitter emitter : emitters) {

            try {

              emitter.send(
                  SseEmitter.event()
                      .name(eventName)
                      .data(data)
              );

            } catch (IOException e) {

              emitter.complete();
              repository.remove(receiverId, emitter);
            }
          }
        }
    );
  }

  @Scheduled(fixedDelay = 1000 * 60 * 30)
  public void cleanUp() {

    repository.findAll().forEach(
        (receiverId, emitters) -> {

          emitters.removeIf(
              emitter -> !ping(emitter)
          );

          if (emitters.isEmpty()) {
            repository.findAll().remove(receiverId);
          }
        }
    );
  }

  private boolean ping(SseEmitter emitter) {

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
}