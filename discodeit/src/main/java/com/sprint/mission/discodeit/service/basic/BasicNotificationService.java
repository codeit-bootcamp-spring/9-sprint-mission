package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.config.CacheConfig;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;

  @Transactional(readOnly = true)
  @Override
  @Cacheable(cacheNames = CacheConfig.NOTIFICATIONS, key = "#receiverId")
  public List<NotificationDto> findAllByReceiverId(UUID receiverId) {
    return notificationRepository.findAllByReceiverIdOrderByCreatedAtDesc(receiverId).stream()
        .map(notificationMapper::toDto)
        .toList();
  }

  @Transactional
  @Override
  @CacheEvict(cacheNames = CacheConfig.NOTIFICATIONS, key = "#requesterId")
  public void delete(UUID notificationId, UUID requesterId) {
    UUID receiverId = notificationRepository.findReceiverIdById(notificationId)
        .orElseThrow(() -> NotificationNotFoundException.withId(notificationId));
    if (!receiverId.equals(requesterId)) {
      throw new AccessDeniedException("Only receiver can delete notification");
    }
    notificationRepository.deleteById(notificationId);
  }
}
