package com.sprint.mission.discodeit.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class SseEmitterRepository {
    private final ConcurrentMap<UUID, List<SseEmitter>> data = new ConcurrentHashMap<>();
    public void save(UUID receiverId, SseEmitter emitter) {
        data.computeIfAbsent(receiverId, k -> new CopyOnWriteArrayList<>())
                .add(emitter);

    }

    public List<SseEmitter> find(UUID userId) {
        return data.get(userId);
    }

    public void delete(UUID userId, SseEmitter emitter) {
        List<SseEmitter> emitters = data.get(userId);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                data.remove(userId);
            }
        }
    }
    public ConcurrentMap<UUID, List<SseEmitter>> findAll() {
        return data;
    }
}
