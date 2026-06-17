package com.sprint.mission.discodeit.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "discodeit.kafka.enabled", havingValue = "true")
public class NotificationRequiredTopicListener {

  private final NotificationService notificationService;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = "discodeit.MessageCreatedEvent", groupId = "discodeit")
  public void onMessageCreated(String payload) {
    try {
      MessageCreatedEvent event = objectMapper.readValue(payload, MessageCreatedEvent.class);
      String channelDisplay = event.channelName() != null ? "#" + event.channelName() : "#(알 수 없는 채널)";
      String title = event.authorUsername() + " (" + channelDisplay + ")";

      readStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(event.channelId())
          .stream()
          .map(ReadStatus::getUser)
          .filter(user -> !user.getId().equals(event.authorId()))
          .forEach(user -> {
            try {
              notificationService.create(user.getId(), title, event.content());
            } catch (Exception e) {
              log.error("메시지 알림 생성 실패: receiverId={}", user.getId(), e);
            }
          });
    } catch (JsonProcessingException e) {
      log.error("MessageCreatedEvent 역직렬화 실패", e);
    }
  }

  @KafkaListener(topics = "discodeit.RoleUpdatedEvent", groupId = "discodeit")
  public void onRoleUpdated(String payload) {
    try {
      RoleUpdatedEvent event = objectMapper.readValue(payload, RoleUpdatedEvent.class);
      String title = "권한이 변경되었습니다.";
      String content = event.oldRole().name() + " -> " + event.newRole().name();
      notificationService.create(event.userId(), title, content);
    } catch (JsonProcessingException e) {
      log.error("RoleUpdatedEvent 역직렬화 실패", e);
    }
  }

  @KafkaListener(topics = "discodeit.S3UploadFailedEvent", groupId = "discodeit")
  public void onS3UploadFailed(String payload) {
    try {
      S3UploadFailedEvent event = objectMapper.readValue(payload, S3UploadFailedEvent.class);
      String title = "파일 업로드 최종 실패";
      String content = String.format("RequestId: %s, BinaryContentId: %s, Error: %s",
          event.requestId(), event.binaryContentId(), event.errorMessage());

      userRepository.findAllByRole(Role.ADMIN).forEach(admin -> {
        try {
          notificationService.create(admin.getId(), title, content);
        } catch (Exception e) {
          log.error("관리자 알림 생성 실패: adminId={}", admin.getId(), e);
        }
      });
    } catch (JsonProcessingException e) {
      log.error("S3UploadFailedEvent 역직렬화 실패", e);
    }
  }
}
