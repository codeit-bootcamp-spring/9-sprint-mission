package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.event.message.ChannelCreatedEvent;
import com.sprint.mission.discodeit.event.message.ChannelDeletedEvent;
import com.sprint.mission.discodeit.event.message.ChannelUpdatedEvent;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ChannelSseEventListener {

  private final SseService sseService;

  @TransactionalEventListener
  public void handle(ChannelCreatedEvent event) {
    sseService.broadcast(
        "channels.created",
        event.getData()
    );
  }

  @TransactionalEventListener
  public void handle(ChannelUpdatedEvent event) {
    sseService.broadcast(
        "channels.updated",
        event.getTo()
    );
  }

  @TransactionalEventListener
  public void handle(ChannelDeletedEvent event) {
    sseService.broadcast(
        "channels.deleted",
        event.getData()
    );
  }
}