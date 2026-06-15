package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
//@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

  private final NotificationRepository notificationRepository;
  private final ReadStatusRepository readStatusRepository;
  private final CacheManager cacheManager;
  private final UserRepository userRepository;

  @Async(value = "notificationExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(MessageCreatedEvent event) {
    log.debug("메시지 생성 알림 처리 시작: messageId ={}", event.messageId());

    List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelIdWithUser(
        event.channelId());

    readStatuses.stream()
        .filter(readStatus -> !readStatus.getUser().getId().equals(event.authorId()))
        .forEach(readStatus -> {
          Notification notification = new Notification(
              readStatus.getUser(),
              event.authorName() + " (#" + event.channelName() + ")",
              event.content()
          );
          notificationRepository.save(notification);
          cacheManager.getCache("notifications").evict(readStatus.getUser().getId());
          log.debug("알림 생성: receiverId={}, content={}", readStatus.getUser().getId(),
              event.content());
        });

    log.info("메시지 알림 생성 완료: channelId={}, 알림 수={}", event.channelId(), readStatuses.size());
  }

  @Async(value = "notificationExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void on(RoleUpdatedEvent event) {
    log.debug("권한 변경 알림 처리 시작: userId ={}", event.userId());

    User user = userRepository.findById(event.userId())
        .orElseThrow(() -> UserNotFoundException.withId(event.userId()));

    Notification notification = new Notification(
        user,
        "권한이 변경되었습니다.",
        event.oldRole() + " -> " + event.newRole()
    );
    notificationRepository.save(notification);
    cacheManager.getCache("notifications").evict(event.userId());

    log.info("권한 변경 알림 생성 완료: userId={}", user.getId());
  }
}
