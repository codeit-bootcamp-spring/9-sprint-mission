package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.NotificationType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

  private final NotificationRepository notificationRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;

  /**
   * 새 메시지 알림 — notificationEnabled=true 인 사용자에게만 저장
   */
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handleMessageCreated(MessageCreatedEvent event) {
    log.debug("메시지 알림 처리 시작: channelId={}", event.channelId());

    readStatusRepository.findAllByChannelId(event.channelId()).stream()
        .filter(rs -> rs.isNotificationEnabled())
        .forEach(rs -> {
          String content = String.format("[%s] 새로운 메시지가 도착했습니다.",
              event.message().getChannel().getName());
          Notification notification = new Notification(
              rs.getUser(),
              NotificationType.MESSAGE_CREATED,
              content,
              event.messageId()
          );
          notificationRepository.save(notification);
          log.info("메시지 알림 저장: userId={}, messageId={}",
              rs.getUser().getId(), event.messageId());
        });
  }

  /**
   * 권한 변경 알림 — 해당 사용자에게 저장
   */
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handleRoleUpdated(RoleUpdatedEvent event) {
    log.debug("권한 변경 알림 처리 시작: userId={}", event.userId());

    User user = userRepository.findById(event.userId()).orElse(null);
    if (user == null) {
      log.warn("권한 변경 알림 대상 유저 없음: userId={}", event.userId());
      return;
    }

    String content = String.format("권한이 %s에서 %s로 변경되었습니다.",
        event.oldRole(), event.newRole());
    Notification notification = new Notification(
        user,
        NotificationType.ROLE_UPDATED,
        content,
        event.userId()
    );
    notificationRepository.save(notification);
    log.info("권한 변경 알림 저장: userId={}", event.userId());
  }
}
