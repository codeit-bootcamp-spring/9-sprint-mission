package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.NotificationEventHandler;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@ConditionalOnProperty(
    prefix = "discodeit.notification.kafka",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true
)
public class NotificationRequiredTopicListener {

  private final NotificationEventHandler notificationEventHandler;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = KafkaEventTopics.MESSAGE_CREATED)
  public void onMessageCreatedEvent(String kafkaEvent) {
    MessageCreatedEvent event = readEvent(kafkaEvent, MessageCreatedEvent.class);
    notificationEventHandler.handle(event);
  }

  @KafkaListener(topics = KafkaEventTopics.ROLE_UPDATED)
  public void onRoleUpdatedEvent(String kafkaEvent) {
    RoleUpdatedEvent event = readEvent(kafkaEvent, RoleUpdatedEvent.class);
    notificationEventHandler.handle(event);
  }

  @KafkaListener(topics = KafkaEventTopics.S3_UPLOAD_FAILED)
  public void onS3UploadFailedEvent(String kafkaEvent) {
    S3UploadFailedEvent event = readEvent(kafkaEvent, S3UploadFailedEvent.class);
    notificationEventHandler.handle(event);
  }

  private <T> T readEvent(String kafkaEvent, Class<T> eventType) {
    try {
      return objectMapper.readValue(kafkaEvent, eventType);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException(
          "Kafka event deserialization failed. eventType=" + eventType.getSimpleName(), e);
    }
  }
}
