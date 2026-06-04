package com.sprint.mission.discodeit.service.basic;

import org.springframework.security.access.AccessDeniedException;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.exception.NotificationNotFoundException;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

  private final NotificationRepository notificationRepository;

  @Transactional(readOnly = true)
  @Override
  public List<NotificationDto> findAllByReceiverId(UUID receiverId) {
    log.debug("알림 API 조회 시작: receiverId={}", receiverId);

    return notificationRepository.findAllByReceiverId(receiverId).stream()
        .map(n -> new NotificationDto(
            n.getId(),
            n.getCreatedAt(),
            n.getReceiver().getId(),
            n.getTitle(),
            n.getContent()
        ))
        .toList();
  }

  @Transactional
  @Override
  public void checkAndDelete(UUID notificationId, UUID requesterId) {
    log.debug("알림 확인 및 삭제 시작: notificationId={}, requesterId={}", notificationId, requesterId);

    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> NotificationNotFoundException.withId(notificationId));

    if (!notification.getReceiver().getId().equals(requesterId)) {
      throw new AccessDeniedException("본인의 알림만 확인할 수 있습니다.");
    }

    notificationRepository.delete(notification);
    log.info("알림 확인 및 삭제 완료: notificationId={}", notificationId);
  }
}
