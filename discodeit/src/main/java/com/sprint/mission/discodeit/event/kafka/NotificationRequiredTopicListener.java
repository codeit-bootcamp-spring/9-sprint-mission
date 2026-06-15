package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRequiredTopicListener {

  private final ObjectMapper objectMapper;
  private final NotificationRepository notificationRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final CacheManager cacheManager;

  @KafkaListener(topics = "discodeit.MessageCreatedEvent")
  @Transactional
  public void onMessageCreatedEvent(String kafkaEvent) {
    try {
      MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);
      log.debug("Kafka 메시지 생성 이벤트 수신: messageId={}", event.messageId());

      List<ReadStatus> readStatuses = readStatusRepository
          .findAllByChannelIdWithUser(event.channelId());

      readStatuses.stream()
          .filter(rs -> !rs.getUser().getId().equals(event.authorId()))
          .forEach(rs -> {
            Notification notification = new Notification(
                rs.getUser(),
                event.authorName() + " (#" + event.channelName() + ")",
                event.content()
            );
            notificationRepository.save(notification);
            cacheManager.getCache("notifications").evict(rs.getUser().getId());
            log.debug("알림 생성: receiverId={}", rs.getUser().getId());
          });

      log.info("Kafka 메시지 알림 생성 완료: channelId={}", event.channelId());

    } catch (JsonProcessingException e) {
      log.error("MessageCreatedEvent 역직렬화 실패: {}", e.getMessage());
      throw new RuntimeException(e);
    }
  }

  @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
  @Transactional
  public void onRoleUpdatedEvent(String kafkaEvent) {
    try {
      RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);
      log.debug("Kafka 권한 변경 이벤트 수신: userId={}", event.userId());

      User user = userRepository.findById(event.userId())
          .orElseThrow(() -> UserNotFoundException.withId(event.userId()));

      Notification notification = new Notification(
          user,
          "권한이 변경되었습니다.",
          event.oldRole() + " -> " + event.newRole()
      );
      notificationRepository.save(notification);
      cacheManager.getCache("notifications").evict(event.userId());

      log.info("Kafka 권한 변경 알림 생성 완료: userId={}", event.userId());

    } catch (JsonProcessingException e) {
      log.error("RoleUpdatedEvent 역직렬화 실패: {}", e.getMessage());
      throw new RuntimeException(e);
    }
  }

  @KafkaListener(topics = "discodeit.S3UploadFailedEvent")
  @Transactional
  public void onS3UploadFailedEvent(String kafkaEvent) {
    try {
      S3UploadFailedEvent event = objectMapper.readValue(kafkaEvent, S3UploadFailedEvent.class);
      log.debug("Kafka S3 업로드 실패 이벤트 수신: binaryContentId={}", event.binaryContentId());
      
      userRepository.findAllByRole(com.sprint.mission.discodeit.entity.UserRole.ADMIN)
          .forEach(admin -> {
            String content = String.join("\n",
                "실패한 작업: S3 파일 업로드",
                "RequestId: " + event.requestId(),
                "BinaryContentId: " + event.binaryContentId(),
                "Error: " + event.errorMessage()
            );
            Notification notification = new Notification(admin, "S3 파일 업로드 실패", content);
            notificationRepository.save(notification);
            cacheManager.getCache("notifications").evict(admin.getId());
            log.info("S3 실패 알림 생성: adminId={}", admin.getId());
          });

    } catch (JsonProcessingException e) {
      log.error("S3UploadFailedEvent 역직렬화 실패: {}", e.getMessage());
      throw new RuntimeException(e);
    }
  }
}