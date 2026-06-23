package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.event.message.BinaryContentUpdatedEvent;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class BinaryContentSseEventListener {

  private final SseService sseService;

  @TransactionalEventListener
  public void handle(BinaryContentUpdatedEvent event) {

    sseService.broadcast(
        "binaryContents.updated",
        event.getTo()
    );
  }
}