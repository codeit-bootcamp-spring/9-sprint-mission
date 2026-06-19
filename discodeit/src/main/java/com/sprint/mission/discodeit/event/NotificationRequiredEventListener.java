package com.sprint.mission.discodeit.event;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
@ConditionalOnProperty(
    prefix = "discodeit.notification.spring-event-listener",
    name = "enabled",
    havingValue = "true"
)
public class NotificationRequiredEventListener {

  private final NotificationEventHandler notificationEventHandler;

  @Async("applicationTaskExecutor")
  @TransactionalEventListener
  public void on(MessageCreatedEvent event) {
    notificationEventHandler.handle(event);
  }

  @Async("applicationTaskExecutor")
  @TransactionalEventListener
  public void on(RoleUpdatedEvent event) {
    notificationEventHandler.handle(event);
  }

  @Async("applicationTaskExecutor")
  @EventListener
  public void on(S3UploadFailedEvent event) {
    notificationEventHandler.handle(event);
  }
}
