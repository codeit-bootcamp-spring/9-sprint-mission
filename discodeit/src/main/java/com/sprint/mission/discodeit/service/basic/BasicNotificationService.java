package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;

  @Transactional(readOnly = true)
  @Cacheable(value = "userNotifications", key = "#receiverId")
  @Override
  public List<NotificationDto> findAllByReceiverId(UUID receiverId) {
    log.info("수신자 알림 목록 조회: receiverId={}", receiverId);
    return notificationRepository.findAllByReceiverId(receiverId).stream()
        .map(notificationMapper::toDto)
        .toList();
  }


  @Transactional
  @CacheEvict(value = "userNotifications", key = "#requestUserId")
  @Override
  public void delete(UUID notificationId, UUID requestUserId) {
    log.info("알림 삭제 요청: id={}, requestUserId={}", notificationId, requestUserId);
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> NotificationNotFoundException.withId(notificationId));

    if (!notification.getReceiverId().equals(requestUserId)) {
      log.warn("알림 소유자 불일치: notification.receiverId={}, requestUserId={}",
          notification.getReceiverId(), requestUserId);
      throw new AccessDeniedException("요청자 본인의 알림에 대해서만 수행할 수 있습니다.");
    }

    notificationRepository.delete(notification);
    log.info("알림 삭제 완료: id={}", notificationId);
  }
}
