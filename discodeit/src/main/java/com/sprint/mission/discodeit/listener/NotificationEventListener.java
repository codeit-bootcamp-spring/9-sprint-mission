package com.sprint.mission.discodeit.listener;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
public class NotificationEventListener {

  private final ReadStatusRepository readStatusRepository;

  public NotificationEventListener(ReadStatusRepository readStatusRepository) {
    this.readStatusRepository = readStatusRepository;
  }

  @EventListener
  public void handleMessageCreatedEvent(MessageCreatedEvent event) {
    List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelIdWithUser(
        event.channelId());

    readStatuses.stream()
        .filter(ReadStatus::isNotificationEnabled)
        .forEach(status -> log.info("이벤트 수신 - 사용자 알림 발송: [대상 User ID: {}], [내용: {}]",
            status.getUser().getId(),
            event.content()
        ));
  }

  @EventListener
  public void handleRoleUpdatedEvent(RoleUpdatedEvent event) {
    log.info("이벤트 수신 - 권한 변경 알림 발송: [대상 User ID: {}], [새로운 권한: {}]",
        event.userId(),
        event.newRole().name()
    );
  }
}