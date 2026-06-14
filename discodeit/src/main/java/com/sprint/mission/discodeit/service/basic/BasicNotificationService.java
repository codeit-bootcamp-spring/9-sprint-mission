package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.notification.NotificationAccessDeniedException;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicNotificationService implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;

  private final CacheManager cacheManager;

  @Override
  @Cacheable(cacheNames = "userNotifications", key = "#receiverId")
  public List<NotificationDto> findAllByReceiverId(UUID receiverId) {
    List<Notification> notifications = notificationRepository.findByReceiverId(receiverId);
    return notifications.stream().map(notificationMapper::toDto).toList();
  }

  @Override
  @Transactional
  @CacheEvict(cacheNames = "userNotifications", key = "#currentUserId")
  public void delete(UUID notificationId, UUID currentUserId) {
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(NotificationNotFoundException::new);

    if (!notification.getReceiverId().equals(currentUserId)) {
      throw new NotificationAccessDeniedException();
    }

    notificationRepository.delete(notification);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void createMessageNotification(UUID uuid) {
    Message message = messageRepository.findById(uuid)
        .orElseThrow(() -> new NoSuchElementException("메시지가 존재하지 않습니다."));

    UUID channelId = message.getChannel().getId();
    UUID authorId = message.getAuthor().getId();
    String channelName = message.getChannel().getName();

    List<ReadStatus> activeReadStatuses = readStatusRepository
        .findByChannelIdAndNotificationEnabledTrue(channelId);

    List<Notification> notifications = activeReadStatuses.stream()
        .map(ReadStatus::getUser)
        .filter(user -> !user.getId().equals(authorId))
        .map(receiver -> new Notification(
            receiver.getId(),
            "보낸 사람 (#" + message.getAuthor().getUsername() + ")",
            message.getContent()
        ))
        .toList();

    notificationRepository.saveAll(notifications);

    Cache cache = cacheManager.getCache("userNotifications");
    if (cache != null) {
      notifications.forEach(n -> cache.evict(n.getReceiverId()));
    }
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @CacheEvict(cacheNames = "userNotifications", key = "#uuid")
  public void createRoleUpdatedNotification(UUID uuid) {
    User user = userRepository.findById(uuid)
        .orElseThrow(() -> new NoSuchElementException("유저가 존재하지 않습니다."));

    Notification notification = new Notification(
        user.getId(),
        "권한이 변경되었습니다.",
        "이전권한 -> " + user.getRole()
    );

    notificationRepository.save(notification);
  }

  @Override
  @Transactional
  public void createAdminNotification(String title, String content) {
    List<User> admins = userRepository.findAllByRole(Role.ADMIN);

    if (admins.isEmpty()) {
      log.warn("알림을 수신할 ADMIN 계정이 없습니다. (title: {})", title);
      return;
    }

    List<Notification> notifications = admins.stream()
        .map(admin -> Notification.builder()
            .receiverId(admin.getId())
            .title(title)
            .content(content)
            .build()
        )
        .toList();

    notificationRepository.saveAll(notifications);

    Cache cache = cacheManager.getCache("userNotifications");
    if (cache != null) {
      admins.forEach(admin -> cache.evict(admin.getId()));
    }

    log.info("{} 명의 관리자에게 알림을 발송했습니다. (title: {})", admins.size(), title);
  }

}
