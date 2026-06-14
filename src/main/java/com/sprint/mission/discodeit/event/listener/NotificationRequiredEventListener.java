package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class NotificationRequiredEventListener {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Async("taskExecutor")
  public void on(MessageCreatedEvent event) {
    UUID channelId = event.getChannelId();
    UUID authorId = event.getAuthorId();
    String content = event.getContent();

    // 발신자 이름을 조회
    String authorName = userRepository.findById(authorId)
        .map(User::getUsername)
        .orElse("알 수 없음");

    List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelIdWithUser(channelId);
    for (ReadStatus rs : readStatuses) {
      if (!rs.isNotificationEnabled()) continue;
      User receiver = rs.getUser();
      if (receiver.getId().equals(authorId)) continue; // 발신자 제외
      // 제목 형식: "보낸 사람 (#채널명)" -> 실제로는 "{보낸사람이름} (#채널명)"
      String title = String.format("%s (#%s)", authorName, rs.getChannel().getName());
      String body = content;
      notificationRepository.save(new Notification(receiver, title, body));
    }
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Async("taskExecutor")
  public void on(RoleUpdatedEvent event) {
    UUID userId = event.getUserId();
    User user = userRepository.findById(userId).orElse(null);
    if (user == null) return;
    String title = "권한이 변경되었습니다.";
    String content = String.format("%s -> %s", event.getOldRole().name(), event.getNewRole().name());
    notificationRepository.save(new Notification(user, title, content));
  }
}

