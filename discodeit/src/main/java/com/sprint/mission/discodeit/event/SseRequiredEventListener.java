package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.sse.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class SseRequiredEventListener {

  private final SseService sseService;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(SseSendRequiredEvent event) {
    sseService.send(event.receiverIds(), event.eventName(), event.data());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(SseBroadcastRequiredEvent event) {
    sseService.broadcast(event.eventName(), event.data());
  }
}
