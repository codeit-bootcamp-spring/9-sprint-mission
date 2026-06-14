package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  @Async("eventTaskExecutor")
  @TransactionalEventListener
  public void on(MessageCreatedEvent event) {
    try {
      String payload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send("discodeit.MessageCreatedEvent", payload);
      log.info("Published MessageCreatedEvent to Kafka: messageId={}", event.getMessageId());
    } catch (JsonProcessingException e) {
      log.error("Failed to serialize MessageCreatedEvent", e);
    }
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener
  public void on(RoleUpdatedEvent event) {
    try {
      String payload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send("discodeit.RoleUpdatedEvent", payload);
      log.info("Published RoleUpdatedEvent to Kafka: userId={}", event.getUserId());
    } catch (JsonProcessingException e) {
      log.error("Failed to serialize RoleUpdatedEvent", e);
    }
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener
  public void on(S3UploadFailedEvent event) {
    try {
      String payload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send("discodeit.S3UploadFailedEvent", payload);
      log.info("Published S3UploadFailedEvent to Kafka: binaryContentId={}", event.getBinaryContentId());
    } catch (JsonProcessingException e) {
      log.error("Failed to serialize S3UploadFailedEvent", e);
    }
  }

}
