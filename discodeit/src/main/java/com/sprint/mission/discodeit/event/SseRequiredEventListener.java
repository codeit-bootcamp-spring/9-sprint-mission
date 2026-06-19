package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.service.SseService;
import com.sprint.mission.discodeit.sse.SseEventNames;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class SseRequiredEventListener {

  private final SseService sseService;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(SseNotificationCreatedEvent event) {
    sseService.send(
        List.of(event.receiverId()),
        SseEventNames.NOTIFICATIONS_CREATED,
        event.notification()
    );
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(SseBinaryContentUpdatedEvent event) {
    sseService.broadcast(SseEventNames.BINARY_CONTENTS_UPDATED, event.binaryContent());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(SseChannelChangedEvent event) {
    if (event.isBroadcast()) {
      sseService.broadcast(event.eventName(), event.channel());
      return;
    }
    sseService.send(event.receiverIds(), event.eventName(), event.channel());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(SseUserChangedEvent event) {
    sseService.broadcast(event.eventName(), event.user());
  }
}
