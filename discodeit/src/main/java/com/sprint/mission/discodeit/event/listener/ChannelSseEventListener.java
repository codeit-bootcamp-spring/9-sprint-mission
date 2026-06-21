package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.event.message.ChannelCreatedEvent;
import com.sprint.mission.discodeit.event.message.ChannelDeletedEvent;
import com.sprint.mission.discodeit.event.message.ChannelUpdatedEvent;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ChannelSseEventListener {

  private final SseService sseService;

  @TransactionalEventListener(
      phase = TransactionPhase.AFTER_COMMIT
  )
  public void handle(
      ChannelCreatedEvent event
  ) {
    sseService.broadcast(
        "channels.created",
        event.getData()
    );
  }

  @TransactionalEventListener(
      phase = TransactionPhase.AFTER_COMMIT
  )
  public void handle(
      ChannelUpdatedEvent event
  ) {
    sseService.broadcast(
        "channels.updated",
        event.getTo()
    );
  }

  @TransactionalEventListener(
      phase = TransactionPhase.AFTER_COMMIT
  )
  public void handle(
      ChannelDeletedEvent event
  ) {
    sseService.broadcast(
        "channels.deleted",
        event.getData()
    );
  }
}