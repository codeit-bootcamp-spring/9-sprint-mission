package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.SseEvent;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaSseEventListener {

  private final SseService sseService;
  private final ObjectMapper objectMapper;

  @KafkaListener(
      topics = "discodeit.SseEvent",
      groupId = "sse-group-#{T(java.util.UUID).randomUUID().toString()}"
  )
  public void handleSseEventFromKafka(String kafkaEvent) {
    try {
      SseEvent event = objectMapper.readValue(kafkaEvent, SseEvent.class);
      log.info("KafkaSseEventListener received event: {}, receiverIds size: {}",
          event.eventName(), event.receiverIds() != null ? event.receiverIds().size() : "broadcast");

      if (event.receiverIds() == null) {
        sseService.broadcast(event.eventName(), event.data());
      } else {
        sseService.send(event.receiverIds(), event.eventName(), event.data());
      }
    } catch (Exception e) {
      log.error("Failed to process SseEvent from Kafka", e);
    }
  }
}
