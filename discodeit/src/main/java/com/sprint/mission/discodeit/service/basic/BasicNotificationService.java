package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

  private final NotificationRepository notificationRepository;

  @Cacheable(value = "notifications", key = "#receiverId")
  @Transactional
  @Override
  public NotificationDto create(User receiver, String title, String content) {
    Notification notification = new Notification(receiver, title, content);
    Notification saved = notificationRepository.save(notification);
    log.info("알림 생성 완료 - 수신자: {}, 제목: {}", receiver.getUsername(), title);
    return toDto(saved);
  }

  @Cacheable(value = "notifications", key = "#receiverId")
  @Transactional(readOnly = true)
  @Override
  public List<NotificationDto> findAllByReceiverId(UUID receiverId) {
    return notificationRepository.findAllByReceiver_Id(receiverId)
        .stream()
        .map(this::toDto)
        .toList();
  }

  @Cacheable(value = "notifications", key = "#receiverId")
  @Transactional
  @Override
  public void delete(UUID notificationId, UUID requestUserId) {
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new NoSuchElementException("알림을 찾을 수 없습니다: " + notificationId));

    // 본인 알림만 삭제 가능
    if (!notification.getReceiver().getId().equals(requestUserId)) {
      throw new SecurityException("본인의 알림만 삭제할 수 있습니다.");
    }

    notificationRepository.deleteById(notificationId);
    log.info("알림 삭제 완료 - 알림 ID: {}", notificationId);
  }

  private NotificationDto toDto(Notification notification) {
    return new NotificationDto(
        notification.getId(),
        notification.getCreatedAt(),
        notification.getReceiver().getId(),
        notification.getTitle(),
        notification.getContent()
    );
  }
}