package com.sprint.mission.discodeit.listener;


import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import java.util.UUID;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class WebSocketRequiredEventListener {

  private final SimpMessagingTemplate messagingTemplate;

  public WebSocketRequiredEventListener(SimpMessagingTemplate messagingTemplate) {
    this.messagingTemplate = messagingTemplate;
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleMessage(MessageCreatedEvent event) {

    UUID channelId = event.channelId();

    String destination = String.format("/sub/channels.%d.messages", channelId);

    messagingTemplate.convertAndSend(destination, event);
  }
}
