package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.config.CacheConfig;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class NotificationRequiredEventListener {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;

  @Async("applicationTaskExecutor")
  @CacheEvict(cacheNames = CacheConfig.NOTIFICATIONS_BY_RECEIVER, allEntries = true)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener
  public void on(MessageCreatedEvent event) {
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
  }

  @Async("applicationTaskExecutor")
  @CacheEvict(cacheNames = CacheConfig.NOTIFICATIONS_BY_RECEIVER, key = "#event.userId")
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener
  public void on(RoleUpdatedEvent event) {
    User receiver = userRepository.findById(event.userId())
        .orElseThrow(() -> new UserNotFoundException(Map.of("userId", event.userId())));

    notificationRepository.save(new Notification(
        receiver,
        "권한이 변경되었습니다.",
        event.previousRole() + " -> " + event.updatedRole()
    ));
  }
}
