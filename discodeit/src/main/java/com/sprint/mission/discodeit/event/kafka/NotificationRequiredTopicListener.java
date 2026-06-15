package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.config.CacheConfig;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.event.SseSendRequiredEvent;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "discodeit.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class NotificationRequiredTopicListener {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;
  private final CacheManager cacheManager;
  private final ObjectMapper objectMapper;
  private final NotificationMapper notificationMapper;
  private final ApplicationEventPublisher eventPublisher;

  @KafkaListener(topics = KafkaProduceRequiredEventListener.MESSAGE_CREATED_TOPIC)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void onMessageCreatedEvent(String kafkaEvent) {
    MessageCreatedEvent event = read(kafkaEvent, MessageCreatedEvent.class);
    List<Notification> notifications = readStatusRepository
        .findAllNotificationEnabledByChannelIdWithUser(event.channelId()).stream()
        .filter(readStatus -> !readStatus.getUser().getId().equals(event.authorId()))
        .map(readStatus -> new Notification(
            readStatus.getUser(),
            event.authorUsername() + " (#" + channelName(event) + ")",
            event.content()
        ))
        .toList();

    notificationRepository.saveAll(notifications)
        .forEach(this::sendNotificationCreated);
  }

  @KafkaListener(topics = KafkaProduceRequiredEventListener.ROLE_UPDATED_TOPIC)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void onRoleUpdatedEvent(String kafkaEvent) {
    RoleUpdatedEvent event = read(kafkaEvent, RoleUpdatedEvent.class);
    User receiver = userRepository.findById(event.userId())
        .orElseThrow(() -> UserNotFoundException.withId(event.userId()));
    Notification notification = notificationRepository.save(new Notification(
        receiver,
        "Role updated",
        event.previousRole() + " -> " + event.newRole()
    ));
    sendNotificationCreated(notification);
  }

  @KafkaListener(topics = KafkaProduceRequiredEventListener.S3_UPLOAD_FAILED_TOPIC)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void onS3UploadFailedEvent(String kafkaEvent) {
    S3UploadFailedEvent event = read(kafkaEvent, S3UploadFailedEvent.class);
    String content = """
        Task: %s
        RequestId: %s
        BinaryContentId: %s
        Error: %s
        """.formatted(
        event.taskName(),
        event.requestId(),
        event.binaryContentId(),
        event.errorMessage()
    );

    List<User> admins = userRepository.findAllByRole(Role.ADMIN);
    List<Notification> notifications = admins.stream()
        .map(admin -> new Notification(admin, "S3 file upload failed", content))
        .toList();
    notificationRepository.saveAll(notifications)
        .forEach(this::sendNotificationCreated);
  }

  private <T> T read(String kafkaEvent, Class<T> eventType) {
    try {
      return objectMapper.readValue(kafkaEvent, eventType);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to deserialize Kafka event", e);
    }
  }

  private String channelName(MessageCreatedEvent event) {
    return event.channelName() == null ? event.channelId().toString() : event.channelName();
  }

  private void evictNotificationCache(Object receiverId) {
    Cache cache = cacheManager.getCache(CacheConfig.NOTIFICATIONS);
    if (cache != null) {
      cache.evict(receiverId);
    }
  }

  private void sendNotificationCreated(Notification notification) {
    NotificationDto dto = notificationMapper.toDto(notification);
    evictNotificationCache(dto.receiverId());
    eventPublisher.publishEvent(
        new SseSendRequiredEvent(List.of(dto.receiverId()), "notifications.created", dto));
  }
}
