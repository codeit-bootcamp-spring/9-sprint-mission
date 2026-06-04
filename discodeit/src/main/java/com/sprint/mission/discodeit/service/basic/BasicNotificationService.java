package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.response.NotificationDto;
import com.sprint.mission.discodeit.config.CacheConfig;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BasicNotificationService implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;

  @Override
  @Cacheable(cacheNames = CacheConfig.NOTIFICATIONS_BY_RECEIVER, key = "#receiverId")
  public List<NotificationDto> findAllByReceiverId(UUID receiverId) {
    return notificationRepository.findAllByReceiverIdWithReceiver(receiverId).stream()
        .map(notificationMapper::toDto)
        .toList();
  }

  @Transactional
  @Override
  @CacheEvict(cacheNames = CacheConfig.NOTIFICATIONS_BY_RECEIVER, key = "#requesterId")
  public void delete(UUID notificationId, UUID requesterId) {
    Notification notification = notificationRepository.findByIdWithReceiver(notificationId)
        .orElseThrow(() -> new DiscodeitException(ErrorCode.NOTIFICATION_NOT_FOUND,
            Map.of("notificationId", notificationId)));

    if (!notification.getReceiver().getId().equals(requesterId)) {
      throw new DiscodeitException(ErrorCode.ACCESS_DENIED,
          Map.of("notificationId", notificationId, "requesterId", requesterId));
    }

    notificationRepository.delete(notification);
  }
}
