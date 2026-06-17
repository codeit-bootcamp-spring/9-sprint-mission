package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {

  private final ObjectMapper objectMapper;

  @KafkaListener(topics = "discodeit.MessageCreatedEvent", groupId = "notification-group")
  public void onMessageCreatedEvent(String kafkaEvent) {
    try {
      MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);
      log.info("카프카 수신 성공 [MessageCreatedEvent]: {}", event.messageId());

    } catch (JsonProcessingException e) {
      log.error("MessageCreatedEvent 직렬화 실패: {}", kafkaEvent, e);
      throw new RuntimeException("Kafka 메시지 변환 에러", e);
    }
  }

  @KafkaListener(topics = "discodeit.RoleUpdatedEvent", groupId = "notification-group")
  public void onRoleUpdatedEvent(String kafkaEvent) {
    try {
      RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);
      log.info("카프카 수신 성공 [RoleUpdatedEvent]: {}", event.userId());

    } catch (JsonProcessingException e) {
      log.error("RoleUpdatedEvent 직렬화 실패: {}", kafkaEvent, e);
    }
  }

  @KafkaListener(topics = "discodeit.S3UploadFailedEvent", groupId = "notification-group")
  public void onS3UploadFailedEvent(String kafkaEvent) {
    try {
      BinaryContentCreatedEvent event = objectMapper.readValue(kafkaEvent, BinaryContentCreatedEvent.class);
      log.info("카프카 수신 성공 [S3UploadFailedEvent]: {}", event.fileName());

    } catch (JsonProcessingException e) {
      log.error("S3UploadFailedEvent 직렬화 실패: {}", kafkaEvent, e);
    }
  }
}
