package com.sprint.mission.discodeit.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class SseEventListener {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleSseEvent(SseEvent event) {
    log.info("SSE Event Listener publishing SseEvent to Kafka: {}", event.eventName());
    try {
      String payload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send("discodeit.SseEvent", payload);
    } catch (JsonProcessingException e) {
      log.error("Failed to serialize SseEvent for Kafka publication", e);
    }
  }
}
