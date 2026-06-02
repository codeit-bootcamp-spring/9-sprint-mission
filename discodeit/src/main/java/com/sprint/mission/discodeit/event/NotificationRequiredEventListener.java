package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

  private final NotificationRepository notificationRepository;
  private final ReadStatusRepository readStatusRepository;
  private final CacheManager cacheManager;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void on(MessageCreatedEvent event) {
    log.debug("메시지 생성 알림 처리 시작: messageId ={}", event.message().getId());

    UUID channelId = event.message().getChannel().getId();
    UUID authorId = event.message().getAuthor().getId();
    String channelName = event.message().getChannel().getName();
    String authorName = event.message().getAuthor().getUsername();
    String content = event.message().getContent();

    List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelIdWithUser(channelId);

    readStatuses.stream()
        .filter(readStatus -> !readStatus.getUser().getId().equals(authorId))
        .forEach(readStatus -> {
          Notification notification = new Notification(
              readStatus.getUser(),
              authorName + " (#" + channelName + ")",
              content
          );
          notificationRepository.save(notification);
          cacheManager.getCache("notifications").evict(readStatus.getUser().getId());
          log.debug("알림 생성: receiverId={}, content={}", readStatus.getUser().getId(), content);
        });

    log.info("메시지 알림 생성 완료: channelId={}, 알림 수={}", channelId, readStatuses.size());
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void on(RoleUpdatedEvent event) {
    log.debug("권한 변경 알림 처리 시작: userId ={}", event.user().getId());

    User user = event.user();
    String title = "권한이 변경되었습니다";
    String content = event.oldRole() + " -> " + event.newRole();

    Notification notification = new Notification(user, title, content);
    notificationRepository.save(notification);

    log.info("권한 변경 알림 생성 완료: userId={}", user.getId());
  }
}
