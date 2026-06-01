package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.exception.notification.UnauthorizedNotificationException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicNotificationService implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;

  @Transactional(readOnly = true)
  @Override
  public List<NotificationDto> findAllByReceiverId(UUID receiverId) {
    log.debug("알림 목록 조회: receiverId={}", receiverId);
    return notificationRepository.findAllByReceiverId(receiverId).stream()
        .map(notificationMapper::toDto)
        .toList();
  }

  @Transactional
  @Override
  public void delete(UUID notificationId, UUID requesterId) {
    log.debug("알림 삭제 시작: id={}, requesterId={}", notificationId, requesterId);

    var notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> NotificationNotFoundException.withId(notificationId));

    // 본인 알림인지 확인
    if (!notification.getReceiver().getId().equals(requesterId)) {
      throw UnauthorizedNotificationException.withId(notificationId);
    }

    notificationRepository.deleteById(notificationId);
    log.info("알림 삭제 완료: id={}", notificationId);
  }
}
