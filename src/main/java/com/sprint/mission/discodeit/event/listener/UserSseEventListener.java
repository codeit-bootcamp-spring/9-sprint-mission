package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.event.message.UserCreatedEvent;
import com.sprint.mission.discodeit.event.message.UserDeletedEvent;
import com.sprint.mission.discodeit.event.message.UserUpdatedEvent;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UserSseEventListener {

  private final SseService sseService;

  @TransactionalEventListener
  public void handle(UserCreatedEvent event) {

    sseService.broadcast(
        "users.created",
        event.getData()
    );
  }

  @TransactionalEventListener
  public void handle(UserUpdatedEvent event) {

    sseService.broadcast(
        "users.updated",
        event.getTo()
    );
  }

  @TransactionalEventListener
  public void handle(UserDeletedEvent event) {

    sseService.broadcast(
        "users.deleted",
        event.getData()
    );
  }
}