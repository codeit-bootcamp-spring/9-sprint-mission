package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.data.SseMessage;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Repository
public class SseMessageRepository {
    private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
    private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

    public UUID save(SseMessage message) {
        UUID eventId = UUID.randomUUID();
        eventIdQueue.add(eventId);
        messages.put(eventId, message);
        return eventId;
    }

    public List<SseMessage> findUnsentMessages(UUID lastEventId) {
        List<SseMessage> result = new ArrayList<>();

        boolean startCollecting = false;

        for (UUID id : eventIdQueue) {
            if (startCollecting) {
                result.add(messages.get(id));
            }

            if (id.equals(lastEventId)) {
                startCollecting = true;
            }
        }

        return result;
    }
}
