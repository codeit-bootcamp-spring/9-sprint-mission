package com.sprint.mission.discodeit.listener;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;

import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  private final ReadStatusRepository readStatusRepository;
  private final NotificationRepository notificationRepository;
  private final MessageRepository messageRepository;

  @Async("taskExecutor")
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(MessageCreatedEvent event) {
    log.debug("MessageCreatedEvent 수신: messageId={}", event.messageId());

    Message message = messageRepository.findById(event.messageId())
        .orElseThrow(
            () -> new IllegalArgumentException("해당 메시지를 찾을 수 없습니다. ID: " + event.messageId()));

    UUID channelId = message.getChannel().getId();
    UUID senderId = message.getAuthor().getId();
    String senderName = message.getAuthor().getUsername();
    String channelName = message.getChannel().getName();

    List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelIdWithUser(channelId);

    readStatuses.stream()
        .filter(ReadStatus::isNotificationEnabled)
        .filter(status -> !status.getUser().getId().equals(senderId))
        .forEach(status -> {

          String title = String.format("%s (#%s)", senderName, channelName);
          String content = message.getContent();

          Notification notification = new Notification(
              status.getUser(),
              title,
              content
          );

          notificationRepository.save(notification);
          log.info("메시지 알림 생성 완료: receiverId={}, title={}", status.getUser().getId(), title);
        });
  }

  @Async("taskExecutor")
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(RoleUpdatedEvent event) {
    log.debug("RoleUpdatedEvent 수신: userId={}, newRole={}", event.userId(), event.newRole());

    readStatusRepository.findAllByUserId(event.userId()).stream()
        .findFirst()
        .ifPresent(status -> {
          com.sprint.mission.discodeit.entity.User user = status.getUser();

          String previousRoleName = "USER";
          String newRoleName = event.newRole().name();

          String title = "권한이 변경되었습니다.";
          String content = String.format("%s -> %s", previousRoleName, newRoleName);

          Notification notification = new Notification(
              user,
              title,
              content
          );

          notificationRepository.save(notification);
          log.info("권한 변경 알림 생성 및 DB 저장 완료: receiverId={}, content={}", event.userId(), content);
        });
  }
}