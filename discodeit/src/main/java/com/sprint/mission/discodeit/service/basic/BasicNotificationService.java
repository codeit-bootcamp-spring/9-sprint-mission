package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;

  @Cacheable(value = "userNotifications", key = "#receiverId")
  @Override
  public List<NotificationDto> findAllByReceiverId(UUID receiverId) {
    return notificationRepository.findAllByReceiverId(receiverId).stream()
        .map(notificationMapper::toDto)
        .toList();
  }

  @CacheEvict(value = "userNotifications", key = "#receiverId")
  @Override
  public void delete(UUID notificationId, UUID receiverId) {
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new NoSuchElementException("Notification not found"));

    if (!notification.getReceiver().getId().equals(receiverId)) {
      throw new AccessDeniedException("본인의 알림만 삭제할 수 있습니다.");
    }

    notificationRepository.deleteById(notificationId);
  }
}