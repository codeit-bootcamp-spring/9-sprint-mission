package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.config.CacheConfig;
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
import com.sprint.mission.discodeit.storage.BinaryContentUploadFailureNotifier;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;
  private final BinaryContentUploadFailureNotifier failureNotifier;
  private final ObjectMapper objectMapper;

  @CacheEvict(cacheNames = CacheConfig.NOTIFICATIONS_BY_RECEIVER, allEntries = true)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @KafkaListener(topics = KafkaEventTopics.MESSAGE_CREATED)
  public void onMessageCreatedEvent(String kafkaEvent) {
    MessageCreatedEvent event = readEvent(kafkaEvent, MessageCreatedEvent.class);
    List<Notification> notifications = readStatusRepository
        .findAllNotificationEnabledByChannelIdWithUser(event.channelId())
        .stream()
        .map(ReadStatus::getUser)
        .filter(user -> !user.getId().equals(event.authorId()))
        .map(user -> new Notification(
            user,
            event.authorUsername() + " (#" + event.channelName() + ")",
            event.content()
        ))
        .toList();

    notificationRepository.saveAll(notifications);
    log.info("Message notification created. messageId={}, count={}",
        event.messageId(), notifications.size());
  }

  @CacheEvict(cacheNames = CacheConfig.NOTIFICATIONS_BY_RECEIVER, allEntries = true)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @KafkaListener(topics = KafkaEventTopics.ROLE_UPDATED)
  public void onRoleUpdatedEvent(String kafkaEvent) {
    RoleUpdatedEvent event = readEvent(kafkaEvent, RoleUpdatedEvent.class);
    User receiver = userRepository.findById(event.userId())
        .orElseThrow(() -> new UserNotFoundException(Map.of("userId", event.userId())));

    notificationRepository.save(new Notification(
        receiver,
        "권한이 변경되었습니다.",
        event.previousRole() + " -> " + event.updatedRole()
    ));
    log.info("Role update notification created. userId={}", event.userId());
  }

  @CacheEvict(cacheNames = CacheConfig.NOTIFICATIONS_BY_RECEIVER, allEntries = true)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @KafkaListener(topics = KafkaEventTopics.S3_UPLOAD_FAILED)
  public void onS3UploadFailedEvent(String kafkaEvent) {
    S3UploadFailedEvent event = readEvent(kafkaEvent, S3UploadFailedEvent.class);
    failureNotifier.notifyAdmins(event);
    log.info("S3 upload failure notification created. binaryContentId={}",
        event.binaryContentId());
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
