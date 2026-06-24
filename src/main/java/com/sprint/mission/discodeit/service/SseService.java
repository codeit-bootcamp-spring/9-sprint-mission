package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SseService {
    private final SseEmitterRepository repository;

    public SseEmitter connect(UUID receiverId, UUID lastEventId) {
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);
        emitter.onCompletion(() -> repository.delete(receiverId, emitter));
        emitter.onTimeout(() -> repository.delete(receiverId, emitter));
        emitter.onError((e) -> repository.delete(receiverId, emitter));

        ping(emitter);
        repository.save(receiverId, emitter);
        return emitter;
    }

    public void send(Collection<UUID> receiverIds, String eventName, Object data) {
        for (UUID receiverId : receiverIds) {
            if (receiverId == null) continue;

            List<SseEmitter> emitters = repository.find(receiverId);
            if (emitters == null) continue;

            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event().name(eventName).data(data));
                } catch (IOException e) {
                    repository.delete(receiverId, emitter);
                }
            }
        }
    }

    public void broadcast(String eventName, Object data) {
        for (Map.Entry<UUID, List<SseEmitter>> entry : repository.findAll().entrySet()) {
            UUID receiverId = entry.getKey();
            List<SseEmitter> emitters = entry.getValue();

            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event().name(eventName).data(data));
                } catch (IOException e) {
                    repository.delete(receiverId, emitter);
                }
            }
        }
    }

    @Scheduled(fixedDelay = 1000 * 60 * 30)
    public void cleanUp() {
        for (Map.Entry<UUID, List<SseEmitter>> entry : repository.findAll().entrySet()) {
            UUID receiverId = entry.getKey();
            List<SseEmitter> emitters = entry.getValue();

            for (SseEmitter emitter : emitters) {
                if (!ping(emitter)) {
                    repository.delete(receiverId, emitter);
                }
            }
        }
    }

    private boolean ping(SseEmitter sseEmitter) {
        try {
        sseEmitter.send(SseEmitter.event()
                .name("INIT")
                .data("connected!"));
        return true;
    } catch (Exception e) {
        return false;
    }}
}
