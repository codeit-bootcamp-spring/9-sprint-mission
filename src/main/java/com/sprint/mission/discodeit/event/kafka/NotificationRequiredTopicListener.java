package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = "discodeit.MessageCreatedEvent")
  public void onMessageCreatedEvent(String kafkaEvent) {
    try {
      MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);
      log.info("Received MessageCreatedEvent from Kafka: messageId={}", event.getMessageId());

      // 발신자 이름
      String authorName = userRepository.findById(event.getAuthorId()).map(User::getUsername).orElse("알 수 없음");

      List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelIdWithUser(event.getChannelId());
      for (ReadStatus rs : readStatuses) {
        if (!rs.isNotificationEnabled()) continue;
        User receiver = rs.getUser();
        if (receiver.getId().equals(event.getAuthorId())) continue;
        String title = String.format("%s (#%s)", authorName, rs.getChannel().getName());
        notificationRepository.save(new Notification(receiver, title, event.getContent()));
      }
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
  public void onRoleUpdatedEvent(String kafkaEvent) {
    try {
      RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);
      log.info("Received RoleUpdatedEvent from Kafka: userId={}", event.getUserId());
      userRepository.findById(event.getUserId()).ifPresent(user -> {
        String title = "권한이 변경되었습니다.";
        String content = String.format("%s -> %s", event.getOldRole().name(), event.getNewRole().name());
        notificationRepository.save(new Notification(user, title, content));
      });
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @KafkaListener(topics = "discodeit.S3UploadFailedEvent")
  public void onS3UploadFailedEvent(String kafkaEvent) {
    try {
      S3UploadFailedEvent event = objectMapper.readValue(kafkaEvent, S3UploadFailedEvent.class);
      log.info("Received S3UploadFailedEvent from Kafka: binaryContentId={}", event.getBinaryContentId());
      // 관리자에게 알림 생성
      List<User> admins = userRepository.findAllByRole(com.sprint.mission.discodeit.entity.Role.ADMIN);
      String title = "Binary upload failed";
      String content = String.format("BinaryContentId: %s\nError: %s", event.getBinaryContentId(), event.getErrorMessage());
      for (User admin : admins) {
        try {
          notificationRepository.save(new Notification(admin, title, content));
        } catch (Exception ex) {
          log.error("Failed to create admin notification for S3 failure: adminId={}, error={}", admin.getId(), ex.getMessage(), ex);
        }
      }
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

}

