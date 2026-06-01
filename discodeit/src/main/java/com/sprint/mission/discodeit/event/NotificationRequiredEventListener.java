package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

  private final NotificationService notificationService;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;

  @Async
  @TransactionalEventListener
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void on(MessageCreatedEvent event) {
    String channelDisplay = event.channelName() != null ? "#" + event.channelName() : "#(알 수 없는 채널)";
    String title = event.authorUsername() + " (" + channelDisplay + ")";
    String content = event.content();

    readStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(event.channelId())
        .stream()
        .map(ReadStatus::getUser)
        .filter(user -> !user.getId().equals(event.authorId()))
        .forEach(user -> {
          try {
            notificationService.create(user.getId(), title, content);
          } catch (Exception e) {
            log.error("메시지 알림 생성 실패: receiverId={}", user.getId(), e);
          }
        });
  }

  @Async
  @TransactionalEventListener
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void on(RoleUpdatedEvent event) {
    String title = "권한이 변경되었습니다.";
    String content = event.oldRole().name() + " -> " + event.newRole().name();

    try {
      notificationService.create(event.userId(), title, content);
    } catch (Exception e) {
      log.error("권한 변경 알림 생성 실패: userId={}", event.userId(), e);
    }
  }
}
