package com.sprint.mission.discodeit.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.kafka.KafkaSseEvent;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class SseEventConsumer {

    private final SseService sseService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "sse-events",
            groupId = "sse-instance-#{T(java.util.UUID).randomUUID().toString()}"
    )
    public void consume(String payload) {
        try {
            KafkaSseEvent event = objectMapper.readValue(payload, KafkaSseEvent.class);
            switch (event.getType()) {
                case BROADCAST -> sseService.broadcast(event.getEventName(), event.getData());
                case SEND -> sseService.send(event.getReceiverIds(), event.getEventName(), event.getData());
            }
        } catch (JsonProcessingException e) {
            log.error("SSE 이벤트 역직렬화 실패: {}", payload, e);
        }
    }
}