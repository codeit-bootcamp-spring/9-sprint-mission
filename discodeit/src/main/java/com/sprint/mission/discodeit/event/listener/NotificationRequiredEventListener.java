package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
//@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

  private final NotificationRepository notificationRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;

  @Async("taskExecutor")
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener
  public void on(MessageCreatedEvent event) {
    log.debug("메시지 생성 알림 이벤트 처리 시작: messageId={}", event.messageId());

    Channel channel = channelRepository.findById(event.channelId())
        .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다."));
    User sender = userRepository.findById(event.senderId())
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

    String title = sender.getUsername() + " (#" + channel.getName() + ")";
    String content = event.content();

    List<ReadStatus> readStatuses = readStatusRepository.findAllByChannel_Id(event.channelId()); // 언더바(_) 추가!

    for (ReadStatus status : readStatuses) {
      if (status.isNotificationEnabled()) {
        UUID receiverId = status.getUser().getId();

        if (!receiverId.equals(event.senderId())) {
          Notification notification = new Notification(receiverId, title, content);
          notificationRepository.save(notification);
          log.info("메시지 알림 생성 완료: receiverId={}, title={}", receiverId, title);
        }
      }
    }
  }

  @Async("taskExecutor")
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener
  public void on(RoleUpdatedEvent event) {
    log.debug("권한 변경 알림 이벤트 처리 시작: userId={}", event.userId());

    String title = "권한이 변경되었습니다.";
    String content = event.oldRole() + " -> " + event.newRole();

    Notification notification = new Notification(event.userId(), title, content);
    notificationRepository.save(notification);

    log.info("권한 변경 알림 생성 완료: receiverId={}, content={}", event.userId(), content);
  }
}
