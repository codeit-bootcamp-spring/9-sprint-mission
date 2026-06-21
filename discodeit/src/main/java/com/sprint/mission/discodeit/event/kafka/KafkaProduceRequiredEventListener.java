package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.kafka.MessageCreatedPayload;
import com.sprint.mission.discodeit.dto.kafka.RoleUpdatedPayload;
import com.sprint.mission.discodeit.dto.kafka.S3UploadFailedPayload;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
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

  @Async("eventTaskExecutor")
  @TransactionalEventListener
  public void on(MessageCreatedEvent event) {
    try {
      MessageCreatedPayload messageData = new MessageCreatedPayload(
          event.messageId(),
          event.authorId(),
          event.channelId(),
          event.authorName(),
          event.channelName(),
          event.content()
      );

      String payload = objectMapper.writeValueAsString(messageData);
      kafkaTemplate.send("discodeit.MessageCreatedEvent", payload);
      log.info("Successfully produced MessageCreatedEvent to Kafka");
    } catch (JsonProcessingException e) {
      log.error("Failed to serialize MessageCreatedEvent", e);
      throw new RuntimeException(e);
    }
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener
  public void on(RoleUpdatedEvent event) {
    try {
      RoleUpdatedPayload payloadObj = new RoleUpdatedPayload(
          event.userId(),
          event.oldRole(),
          event.newRole()
      );

      String payload = objectMapper.writeValueAsString(payloadObj);
      kafkaTemplate.send("discodeit.RoleUpdatedEvent", payload);
      log.info("Successfully produced RoleUpdatedEvent to Kafka");
    } catch (JsonProcessingException e) {
      log.error("Failed to serialize RoleUpdatedEvent", e);
      throw new RuntimeException(e);
    }
  }

  @Async("eventTaskExecutor")
  @EventListener
  public void on(S3UploadFailedEvent event) {
    try {
      S3UploadFailedPayload payloadObj = new S3UploadFailedPayload(
          event.receiverId(),
          event.errorMessage()
      );

      String payload = objectMapper.writeValueAsString(payloadObj);
      kafkaTemplate.send("discodeit.S3UploadFailedEvent", payload);
      log.info("Successfully produced S3UploadFailedEvent to Kafka");
    } catch (JsonProcessingException e) {
      log.error("Failed to serialize S3UploadFailedEvent", e);
      throw new RuntimeException(e);
    }
  }
}