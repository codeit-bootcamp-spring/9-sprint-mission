package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.BinaryUploadFailedEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {

  private final ObjectMapper objectMapper;
  private final NotificationService notificationService;

  @KafkaListener(topics = "discodeit.MessageCreatedEvent", groupId = "discodeit-group")
  public void onMessageCreatedEvent(String kafkaEvent) {
    try {
      MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);
      notificationService.createMessageNotification(event.messageId());
      log.info("Kafka 알림 생성 성공 [MessageCreatedEvent]: {}", event);
    } catch (Exception e) {
      log.error("Kafka 메시지 처리 실패 [MessageCreatedEvent]", e);
    }
  }

  @KafkaListener(topics = "discodeit.RoleUpdatedEvent", groupId = "discodeit-group")
  public void onRoleUpdatedEvent(String kafkaEvent) {
    try {
      RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);

      notificationService.createRoleUpdatedNotification(event.userId());

      log.info("Kafka 알림 생성 성공 [RoleUpdatedEvent]: {}", event);

    } catch (Exception e) {
      log.error("Kafka 메시지 처리 실패 [RoleUpdatedEvent]", e);
    }
  }

  @KafkaListener(topics = "discodeit.BinaryUploadFailedEvent", groupId = "discodeit-group")
  public void onS3UploadFailedEvent(String kafkaEvent) {
    try {
      BinaryUploadFailedEvent event = objectMapper.readValue(kafkaEvent, BinaryUploadFailedEvent.class);

      String notificationContent = String.format(
          "RequestId: %s\nBinaryContentId: %s\nError: %s",
          event.requestId(),
          event.binaryContentId(),
          event.errorMessage()
      );

      notificationService.createAdminNotification("파일 업로드 실패", notificationContent);

      log.info("Kafka 알림 생성 성공 [BinaryUploadFailedEvent]: {}", event);

    } catch (Exception e) {
      log.error("Kafka 메시지 처리 실패 [BinaryUploadFailedEvent]", e);
    }
  }
}
