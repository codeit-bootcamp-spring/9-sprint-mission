package com.sprint.mission.discodeit.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketRequiredEventListener {

  private static final String MESSAGE_DESTINATION_FORMAT = "/sub/channels.%s.messages";

  private final SimpMessagingTemplate messagingTemplate;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleMessage(MessageCreatedEvent event) {
    String destination = MESSAGE_DESTINATION_FORMAT.formatted(event.channelId());
    messagingTemplate.convertAndSend(destination, event);
    log.debug("Message event sent over WebSocket: destination={}, messageId={}",
        destination, event.messageId());
  }
}
