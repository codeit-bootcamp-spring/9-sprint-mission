package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.event.message.BinaryContentUpdatedEvent;
import com.sprint.mission.discodeit.event.message.NotificationCreatedEvent;
import com.sprint.mission.discodeit.service.SseService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class SseRequiredEventListener {

  private final SseService sseService;

  @TransactionalEventListener
  public void handleNotificationCreated(
      NotificationCreatedEvent event
  ) {

    NotificationDto notification =
        event.getData();

    sseService.send(
        List.of(notification.receiverId()),
        "notifications.created",
        notification
    );
  }

  @TransactionalEventListener
  public void handle(BinaryContentUpdatedEvent event) {

    sseService.broadcast(
        "binaryContents.updated",
        event.getTo()
    );
  }
}