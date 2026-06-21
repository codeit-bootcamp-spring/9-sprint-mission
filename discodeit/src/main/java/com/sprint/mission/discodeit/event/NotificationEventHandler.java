package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentUploadFailureNotifier;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationEventHandler {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;
  private final BinaryContentUploadFailureNotifier failureNotifier;
  private final NotificationCacheEvictor cacheEvictor;
  private final NotificationMapper notificationMapper;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handle(MessageCreatedEvent event) {
    List<Notification> notifications = readStatusRepository
        .findAllNotificationEnabledByChannelIdWithUser(event.channelId())
        .stream()
        .map(ReadStatus::getUser)
        .filter(user -> !user.getId().equals(event.authorId()))
        .filter(user -> shouldCreate(user.getId(), messageEventKey(event.messageId())))
        .map(user -> new Notification(
            user,
            event.authorUsername() + " (#" + event.channelName() + ")",
            event.content(),
            messageEventKey(event.messageId())
        ))
        .toList();

    if (notifications.isEmpty()) {
      cacheEvictor.evictReceivers(List.of());
      log.info("Message notification skipped. messageId={}, count=0", event.messageId());
      return;
    }

    List<Notification> savedNotifications = notificationRepository.saveAll(notifications);
    cacheEvictor.evictReceivers(savedNotifications.stream()
        .map(Notification::getReceiver)
        .map(User::getId)
        .toList());
    savedNotifications.forEach(notification -> eventPublisher.publishEvent(
        new SseNotificationCreatedEvent(
            notification.getReceiver().getId(),
            notificationMapper.toDto(notification)
        )
    ));
    log.info("Message notification created. messageId={}, count={}",
        event.messageId(), savedNotifications.size());
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handle(RoleUpdatedEvent event) {
    String eventKey = roleEventKey(event);
    User receiver = userRepository.findById(event.userId())
        .orElseThrow(() -> new UserNotFoundException(Map.of("userId", event.userId())));

    if (!shouldCreate(receiver.getId(), eventKey)) {
      log.info("Role update notification skipped by duplicate event. userId={}", event.userId());
      return;
    }

    Notification savedNotification = notificationRepository.save(new Notification(
        receiver,
        "권한이 변경되었습니다.",
        event.previousRole() + " -> " + event.updatedRole(),
        eventKey
    ));
    cacheEvictor.evictReceiver(receiver.getId());
    eventPublisher.publishEvent(new SseNotificationCreatedEvent(
        receiver.getId(),
        notificationMapper.toDto(savedNotification)
    ));
    log.info("Role update notification created. userId={}", event.userId());
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handle(S3UploadFailedEvent event) {
    int count = failureNotifier.notifyAdmins(event, s3UploadFailedEventKey(event.binaryContentId()));
    log.info("S3 upload failure notification created. binaryContentId={}, count={}",
        event.binaryContentId(), count);
  }

  private boolean shouldCreate(UUID receiverId, String eventKey) {
    return eventKey == null || !notificationRepository.existsByReceiverIdAndEventKey(
        receiverId,
        eventKey
    );
  }

  private String messageEventKey(UUID messageId) {
    return "message-created:" + messageId;
  }

  private String roleEventKey(RoleUpdatedEvent event) {
    return "role-updated:%s:%s:%s".formatted(
        event.userId(),
        event.previousRole(),
        event.updatedRole()
    );
  }

  private String s3UploadFailedEventKey(UUID binaryContentId) {
    return "s3-upload-failed:" + binaryContentId;
  }
}
