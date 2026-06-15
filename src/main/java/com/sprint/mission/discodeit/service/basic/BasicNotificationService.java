package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.exception.notification.NotificationForbiddenException;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicNotificationService implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;
  private final CacheManager cacheManager;

  @Cacheable(value = "notifications", key = "#receiverId", unless = "#result.isEmpty()")
  @PreAuthorize("principal.userDto.id == #receiverId")
  @Override
  public List<NotificationDto> findAllByReceiverId(UUID receiverId) {
    log.debug("알림 목록 조회 시작: receiverId={}", receiverId);
    List<NotificationDto> notifications = notificationRepository.findAllByReceiverIdOrderByCreatedAtDesc(
            receiverId)
        .stream()
        .map(notificationMapper::toDto)
        .toList();
    log.info("알림 목록 조회 완료: receiverId={}, 조회된 항목 수={}", receiverId, notifications.size());
    return notifications;
  }

  @CacheEvict(value = "notifications", key = "#receiverId")
  @PreAuthorize("principal.userDto.id == #receiverId")
  @Transactional
  @Override
  public void delete(UUID notificationId, UUID receiverId) {
    log.debug("알림 삭제 시작: id={}, receiverId={}", notificationId, receiverId);
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> NotificationNotFoundException.withId(notificationId));
    if (!notification.getReceiverId().equals(receiverId)) {
      log.warn("알림 삭제 권한 없음: id={}, receiverId={}", notificationId, receiverId);
      throw NotificationForbiddenException.withId(notificationId, receiverId);
    }
    notificationRepository.delete(notification);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @Override
  public void create(Set<UUID> receiverIds, String title, String content) {
    if (receiverIds.isEmpty()) {
      log.warn("알림 생성 요청이 비어있음: receiverIds={}", receiverIds);
      return;
    }
    log.debug("새 알림 생성 시작: receiverIds={}", receiverIds);
    List<Notification> notifications = receiverIds.stream()
        .map(receiverId -> new Notification(
            receiverId,
            title,
            content
        )).toList();
    notificationRepository.saveAll(notifications);
    evictNotificationCache(receiverIds);
    log.info("새 알림 생성 완료: receiverIds={}", receiverIds);
  }

  private void evictNotificationCache(Set<UUID> receiverIds) {
    Cache cache = cacheManager.getCache("notifications");
    if (cache != null) {
      for (UUID receiverId : receiverIds) {
        cache.evict(receiverId);
      }
      log.debug("알림 캐시를 제거했습니다: receiverIds={}", receiverIds);
    } else {
      log.warn("알림 캐시가 존재하지 않습니다.");
    }
  }
} 