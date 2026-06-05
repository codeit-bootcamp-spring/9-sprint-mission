package com.sprint.mission.discodeit.listener;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.entity.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.entity.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.service.basic.BasicNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRequiredTopicListener {

  private final ObjectMapper objectMapper;
  private final BasicNotificationService notificationService;

  @KafkaListener(topics = "discodeit.MessageCreatedEvent", groupId = "notification-group")
  public void onMessageCreatedEvent(String kafkaEvent) {
    try {
      MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);
      log.info("MessageCreatedEvent 수신 - 첨부파일 개수: {}",
          event.attachmentIds() != null ? event.attachmentIds().size() : 0);
      notificationService.createNotificationsForMessage(event);
      log.info("MessageCreatedEvent Kafka 처리 완료");
    } catch (JsonProcessingException e) {
      log.info("역직렬화 실패", e);
      throw new RuntimeException(e);
    }
  }

  @KafkaListener(topics = "discodeit.RoleUpdateEvent", groupId = "notification-group")
  public void onRoleUpdatedEvent(String kafkaEvent) {
    try {
      RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);
      notificationService.createNotificationForRoleUpdate(event);
      log.info("RoleUpdatedEvent Kafka 처리 완료:{}", event.targetId());
    } catch (JsonProcessingException e) {
      log.error("RoleUpdatedEvent 역직렬화 실패:", e);
      throw new RuntimeException(e);
    }
  }

  @KafkaListener(topics = "discodeit.S3UploadFailedEvent", groupId = "notification-group")
  public void onS3UploadFailedEvent(String kafkaEvent) {
    try {
      S3UploadFailedEvent event = objectMapper.readValue(kafkaEvent, S3UploadFailedEvent.class);
      notificationService.createNotificationForS3UploadFailure(event);
      log.info("S3UploadFailedEvent Kafka 처리 완료: {}", event.fileKey());
    } catch (JsonProcessingException e) {
      log.error("S3UploadFailedEvent 역직렬화 실패:", e);
      throw new RuntimeException(e);
    }
  }
}
