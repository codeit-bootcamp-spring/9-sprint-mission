package com.sprint.mission.discodeit.service.basic;


import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.entity.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BasicNotificationService {

  private final NotificationRepository repository;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;


  public List<NotificationDto> getNotification(UUID receiverId) {
    return repository.findAllByReceiverId(receiverId)
        .stream().map(NotificationDto::from)
        .toList();
  }


  @Transactional
  public void deleteNotification(UUID id, UUID receiverId) {
    Notification notification = repository.findByIdAndReceiverId(id, receiverId)
        .orElseThrow(() -> {
          log.warn("알림 삭제 실패: ID={}, 수신자={}", id, receiverId);
          return new NotificationNotFoundException();
        });
    repository.delete(notification);
    log.info("알림 삭제 완료: ID={}, 수신자={}", id, receiverId);

  }

  @Transactional
  public void createNotificationsForMessage(MessageCreatedEvent event) {
    List<ReadStatus> activeReadStatuses = readStatusRepository.findAllByChannelIdAndAlarmEnabled(
        event.channelId(), true);

    List<Notification> notifications = activeReadStatuses.stream()
        .filter(status -> !status.getUser().getId().equals(event.authorId()))
        .map(status -> Notification.builder()
            .receiver(status.getUser())
            .title(event.authorId() + " (" + status.getChannel().getName() + ")")
            .content(event.content())
            .isRead(false)
            .build())
        .toList();

    repository.saveAll(notifications);
  }

  @Transactional
  public void createNotificationForRoleUpdate(RoleUpdatedEvent event) {
    User user = userRepository.findById(event.targetId())
        .orElseThrow(EntityNotFoundException::new);

    Notification notification = Notification.builder()
        .receiver(user)
        .title("권한이 변경되었습니다.")
        .content(event.oldRole() + " -> " + event.newRole())
        .isRead(false)
        .build();

    repository.save(notification);
  }
}
