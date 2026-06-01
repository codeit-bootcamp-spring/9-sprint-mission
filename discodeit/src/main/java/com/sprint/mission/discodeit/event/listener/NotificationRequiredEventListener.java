package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

  private final NotificationService notificationService;

  @TransactionalEventListener
  public void on(MessageCreatedEvent event) {
    notificationService.createMessageNotification(event.messageId());
  }

  @TransactionalEventListener
  public void on(RoleUpdatedEvent event) {
    notificationService.createRoleUpdatedNotification(event.userId());
  }
}
