package com.sprint.mission.discodeit.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {

  private final NotificationService notificationService;
  private final BinaryContentService binaryContentService;
  private final ObjectMapper objectMapper;

  @KafkaListener(
      topics = "discodeit.MessageCreatedEvent",
      groupId = "discodeit-notification-static-group"
  )
  public void onMessageCreatedEvent(String kafkaEvent) {
    try {
      MessageCreatedEvent event = objectMapper.readValue(kafkaEvent,
          MessageCreatedEvent.class);
      notificationService.createByMessage(event.messageDto());
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @KafkaListener(
      topics = "discodeit.RoleUpdatedEvent",
      groupId = "discodeit-notification-static-group"
  )
  public void onRoleUpdatedEvent(String kafkaEvent) {
    try {
      RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent,
          RoleUpdatedEvent.class);
      notificationService.createByRole(event.user(), event.pastRole(), event.newRole());
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @KafkaListener(
      topics = "discodeit.S3UploadFailedEvent",
      groupId = "discodeit-notification-static-group"
  )
  public void onS3UploadFailedEvent(String kafkaEvent) {
    try {
      S3UploadFailedEvent event = objectMapper.readValue(kafkaEvent,
          S3UploadFailedEvent.class);
      String message = String.format(
          "RequestId: %s\nBinaryContentId: %s\nError: %s",
          event.requestId(),
          event.contentId(),
          event.errorMessage()
      );
      log.error("바이너리 저장 최종 실패\n{}", message);
      binaryContentService.updateStatus(event.contentId(), BinaryContentStatus.FAIL);
      notificationService.createByError(message);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}

