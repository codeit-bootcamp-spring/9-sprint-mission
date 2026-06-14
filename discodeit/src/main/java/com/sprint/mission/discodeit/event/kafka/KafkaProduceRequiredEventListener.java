package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.BinaryUploadFailedEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaProduceRequiredEventListener {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  @Async("taskExecutor")
  @TransactionalEventListener
  public void on(MessageCreatedEvent event) {
    try {
      String payload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send("discodeit.MessageCreatedEvent", payload);
      log.info("Kafka 발행 성공 [MessageCreatedEvent]: {}", payload);

    } catch (JsonProcessingException e) {
      log.error("Kafka 발행 실패 [MessageCreatedEvent] - JSON 직렬화 오류", e);
    }
  }

  @Async("taskExecutor")
  @TransactionalEventListener
  public void on(RoleUpdatedEvent event) {
    try {
      String payload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send("discodeit.RoleUpdatedEvent", payload);
      log.info("Kafka 발행 성공 [RoleUpdatedEvent]: {}", payload);

    } catch (JsonProcessingException e) {
      log.error("Kafka 발행 실패 [RoleUpdatedEvent] - JSON 직렬화 오류", e);
    }
  }

  @Async("taskExecutor")
  @EventListener
  public void on(BinaryUploadFailedEvent event) {
    try {
      String payload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send("discodeit.BinaryUploadFailedEvent", payload);
      log.info("Kafka 발행 성공 [BinaryUploadFailedEvent]: {}", payload);

    } catch (JsonProcessingException e) {
      log.error("Kafka 발행 실패 [BinaryUploadFailedEvent] - JSON 직렬화 오류", e);
    }
  }
}
