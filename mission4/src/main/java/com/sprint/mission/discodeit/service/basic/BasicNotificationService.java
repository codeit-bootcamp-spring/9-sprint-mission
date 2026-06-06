package com.sprint.mission.discodeit.service.basic;


import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.entity.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.entity.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.exception.User.UserNotFoundException;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

  @Cacheable(value = "notifications", key = "#receiverId")
  public List<NotificationDto> getNotification(UUID receiverId) {
    return repository.findAllByReceiverId(receiverId)
        .stream().map(NotificationDto::from)
        .toList();
  }


  @Transactional
  @CacheEvict(value = "notifications", key = "#receiverId")
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
    String authorName = userRepository.findById(event.authorId())
        .map(User::getUsername)
        .orElseThrow(() -> new UserNotFoundException("메시지 작성자를 찾을 수 없습니다: " + event.authorId()));

    List<ReadStatus> activeReadStatuses = readStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(
        event.channelId());

    List<Notification> notifications = activeReadStatuses.stream()
        .filter(status -> !status.getUser().getId().equals(event.authorId()))
        .map(status -> Notification.builder()
            .receiver(status.getUser())
            .title(authorName + " (" + status.getChannel().getName() + ")")
            .content(event.content())
            .isRead(false)
            .build())
        .toList();

    repository.saveAll(notifications);
  }

  @Transactional
  public void createNotificationForRoleUpdate(RoleUpdatedEvent event) {
    User user = userRepository.findById(event.targetId())
        .orElseThrow(() -> {
          log.warn("존재하지 않는 유저:{}", event.targetId());
          return new UserNotFoundException("유저가 존재하지 않습니다.");
        });

    Notification notification = Notification.builder()
        .receiver(user)
        .title("권한이 변경되었습니다.")
        .content(event.oldRole() + " -> " + event.newRole())
        .isRead(false)
        .build();

    repository.save(notification);
  }


  @Transactional
  public void createNotificationForS3UploadFailure(S3UploadFailedEvent event) {

    User admin = userRepository.findById(event.uploaderId())
        .orElseThrow(() -> new UserNotFoundException("관리자 계정을 찾을 수 없습니다."));

    Notification notification = Notification.builder()
        .receiver(admin)
        .title("파일 업로드 실패")
        .content("파일 경로: " + event.fileKey() + " 업로드에 실패했습니다.")
        .isRead(false)
        .build();

    repository.save(notification);
  }
}
