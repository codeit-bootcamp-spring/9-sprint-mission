package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
//@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

  private final NotificationService notificationService;
  private final ReadStatusRepository readStatusRepository;

  @Async
  @TransactionalEventListener
  public void on(MessageCreatedEvent event) {
    MessageDto message = event.message();

    String title = message.author().username() + " (#" + message.channelId() + ")";
    String content = message.content();

    List<ReadStatus> notifyTargets = readStatusRepository
        .findAllByChannel_Id(message.channelId())
        .stream()
        .filter(ReadStatus::isNotificationEnabled)
        .filter(rs -> !rs.getUser().getId().equals(message.author().id()))
        .toList();

    notifyTargets.forEach(rs -> {
      User receiver = rs.getUser();
      notificationService.create(receiver, title, content);
      log.info("알림 생성 - 수신자: {}", receiver.getUsername());
    });
  }

  @Async
  @TransactionalEventListener
  public void on(RoleUpdatedEvent event) {
    User user = event.user();
    String content = event.oldRole().name() + " -> " + event.newRole().name();
    notificationService.create(user, "권한이 변경되었습니다.", content);
    log.info("권한 변경 알림 생성 - 대상: {}", user.getUsername());
  }
}