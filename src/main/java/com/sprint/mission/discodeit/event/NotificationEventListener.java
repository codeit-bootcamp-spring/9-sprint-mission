package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.NotificationType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@Profile("!kafka")
@RequiredArgsConstructor
public class NotificationEventListener {

  private final NotificationRepository notificationRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;

  @Async("notificationExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void on(MessageCreatedEvent event) {
    log.debug("메시지 알림 처리 시작: channelId={}, authorId={}",
        event.channelId(), event.authorId());

    List<Notification> notifications = readStatusRepository
        .findAllByChannelIdWithUser(event.channelId())
        .stream()
        .filter(rs -> rs.isNotificationEnabled())
        .filter(rs -> !rs.getUser().getId().equals(event.authorId())) // 발신자 제외
        .map(rs -> new Notification(
            rs.getUser().getId(),
            "새 메시지",
            String.format("[%s] 새로운 메시지가 도착했습니다.", event.channelName())
        ))
        .toList();

    notificationRepository.saveAll(notifications);

    log.info("메시지 알림 저장 완료: channelId={}, 알림 수={}",
        event.channelId(), notifications.size());
  }

  @Async("notificationExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void on(RoleUpdatedEvent event) {
    log.debug("권한 변경 알림 처리 시작: userId={}", event.userId());

    User user = userRepository.findById(event.userId()).orElse(null);
    if (user == null) {
      log.warn("권한 변경 알림 대상 유저 없음: userId={}", event.userId());
      return;
    }

    Notification notification = new Notification(
        user.getId(),
        "권한 변경",
        String.format("권한이 %s에서 %s로 변경되었습니다.", event.oldRole(), event.newRole())
    );
    notificationRepository.save(notification);

    log.info("권한 변경 알림 저장 완료: userId={}", event.userId());
  }
}
