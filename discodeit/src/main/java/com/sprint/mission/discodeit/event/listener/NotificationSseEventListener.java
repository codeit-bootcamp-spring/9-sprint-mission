package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.event.message.NotificationCreatedEvent;
import com.sprint.mission.discodeit.service.SseService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationSseEventListener {

  private final SseService sseService;

  @TransactionalEventListener(
      phase = TransactionPhase.AFTER_COMMIT
  )
  public void handleNotificationCreated(
      NotificationCreatedEvent event
  ) {

    NotificationDto dto =
        event.getData();

    sseService.send(
        Set.of(dto.receiverId()),
        "notifications.created",
        dto
    );
  }
}