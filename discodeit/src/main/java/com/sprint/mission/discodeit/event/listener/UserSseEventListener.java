package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.event.message.UserCreatedEvent;
import com.sprint.mission.discodeit.event.message.UserDeletedEvent;
import com.sprint.mission.discodeit.event.message.UserUpdatedEvent;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UserSseEventListener {

  private final SseService sseService;

  @TransactionalEventListener
  public void on(UserCreatedEvent event) {
    sseService.broadcast(
        "users.created",
        event.getData()
    );
  }

  @TransactionalEventListener
  public void on(UserUpdatedEvent event) {
    sseService.broadcast(
        "users.updated",
        event.getTo()
    );
  }

  @TransactionalEventListener
  public void on(UserDeletedEvent event) {
    sseService.broadcast(
        "users.deleted",
        event.getData()
    );
  }
}