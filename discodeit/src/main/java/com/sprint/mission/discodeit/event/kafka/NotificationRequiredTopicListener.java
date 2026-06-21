package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.kafka.MessageCreatedPayload;
import com.sprint.mission.discodeit.dto.kafka.RoleUpdatedPayload;
import com.sprint.mission.discodeit.dto.kafka.S3UploadFailedPayload;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.event.SseEvent;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {

  private final ReadStatusRepository readStatusRepository;
  private final NotificationRepository notificationRepository;
  private final CacheManager cacheManager;
  private final ObjectMapper objectMapper;
  private final NotificationMapper notificationMapper;
  private final ApplicationEventPublisher eventPublisher;

  @KafkaListener(topics = "discodeit.MessageCreatedEvent")
  @Transactional
  public void onMessageCreatedEvent(String kafkaEvent) {
    try {
      MessageCreatedPayload payload = objectMapper.readValue(kafkaEvent, MessageCreatedPayload.class);

      UUID authorId = payload.authorId();
      UUID channelId = payload.channelId();
      String title = payload.authorName() + " (#" + payload.channelName() + ")";
      String content = payload.content();

      Cache userNotificationsCache = cacheManager.getCache("userNotifications");

      readStatusRepository.findAllByChannelIdWithUser(channelId)
          .stream()
          .filter(rs -> !rs.getUser().getId().equals(authorId) && rs.isNotificationEnabled())
          .forEach(rs -> {
            UUID receiverId = rs.getUser().getId();
            Notification saved = notificationRepository.save(new Notification(receiverId, title, content));
            eventPublisher.publishEvent(new SseEvent(List.of(receiverId), "notifications.created", notificationMapper.toDto(saved)));
            if (userNotificationsCache != null) {
              userNotificationsCache.evict(receiverId);
            }
          });

      log.info("Successfully processed MessageCreatedEvent from Kafka");
    } catch (JsonProcessingException e) {
      log.error("Failed to deserialize MessageCreatedEvent from Kafka", e);
      throw new RuntimeException(e);
    }
  }

  @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
  @Transactional
  public void onRoleUpdatedEvent(String kafkaEvent) {
    try {
      RoleUpdatedPayload payload = objectMapper.readValue(kafkaEvent, RoleUpdatedPayload.class);

      UUID userId = payload.userId();
      String title = "권한이 변경되었습니다.";
      String content = payload.oldRole() + " -> " + payload.newRole();

      Notification saved = notificationRepository.save(new Notification(userId, title, content));
      eventPublisher.publishEvent(new SseEvent(List.of(userId), "notifications.created", notificationMapper.toDto(saved)));
      evictUserNotifications(userId);

      log.info("Successfully processed RoleUpdatedEvent from Kafka");
    } catch (JsonProcessingException e) {
      log.error("Failed to deserialize RoleUpdatedEvent from Kafka", e);
      throw new RuntimeException(e);
    }
  }

  @KafkaListener(topics = "discodeit.S3UploadFailedEvent")
  @Transactional
  public void onS3UploadFailedEvent(String kafkaEvent) {
    try {
      S3UploadFailedPayload payload = objectMapper.readValue(kafkaEvent, S3UploadFailedPayload.class);

      UUID userId = payload.receiverId();
      String title = "S3 파일 업로드 실패";
      String error = payload.errorMessage();

      Notification saved = notificationRepository.save(new Notification(userId, title, error));
      eventPublisher.publishEvent(new SseEvent(List.of(userId), "notifications.created", notificationMapper.toDto(saved)));
      evictUserNotifications(userId);

      log.info("Successfully processed S3UploadFailedEvent from Kafka");
    } catch (JsonProcessingException e) {
      log.error("Failed to deserialize S3UploadFailedEvent from Kafka", e);
      throw new RuntimeException(e);
    }
  }

  private void evictUserNotifications(UUID userId) {
    Cache cache = cacheManager.getCache("userNotifications");
    if (cache != null) {
      cache.evict(userId);
    }
  }
}