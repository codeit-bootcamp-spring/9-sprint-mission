package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.config.CacheConfig;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.sse.SseService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "discodeit.notification.spring-event-listener.enabled", havingValue = "true")
public class NotificationRequiredEventListener {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;
  private final CacheManager cacheManager;
  private final NotificationMapper notificationMapper;
  private final SseService sseService;

  @Async
  @TransactionalEventListener
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void on(MessageCreatedEvent event) {
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

  @Async
  @TransactionalEventListener
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void on(RoleUpdatedEvent event) {
    User receiver = userRepository.findById(event.userId())
        .orElseThrow(() -> UserNotFoundException.withId(event.userId()));
    Notification notification = notificationRepository.save(new Notification(
        receiver,
        "Role updated",
        event.previousRole() + " -> " + event.newRole()
    ));
    sendNotificationCreated(notification);
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
    sseService.send(List.of(dto.receiverId()), "notifications.created", dto);
  }
}
