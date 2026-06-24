package com.sprint.mission.discodeit.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.kafka.KafkaSseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SseEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void broadcast(String eventName, Object data) {
        publish(new KafkaSseEvent(KafkaSseEvent.Type.BROADCAST, eventName, data, null));
    }

    public void send(List<UUID> receiverIds, String eventName, Object data) {
        publish(new KafkaSseEvent(KafkaSseEvent.Type.SEND, eventName, data, receiverIds));
    }

    private void publish(KafkaSseEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("sse-events", payload);
        } catch (JsonProcessingException e) {
            log.error("SSE 이벤트 직렬화 실패: {}", event, e);
        }
    }
}