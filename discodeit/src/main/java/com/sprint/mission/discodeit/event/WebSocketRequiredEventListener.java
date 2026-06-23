package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.mapper.MessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class WebSocketRequiredEventListener {

  private final SimpMessagingTemplate messagingTemplate;
  private final MessageMapper messageMapper;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleMessage(MessageCreatedEvent event) {
    var message = event.getMessage();
    var channelId = message.getChannel().getId();
    var messageDto = messageMapper.toDto(message);

    messagingTemplate.convertAndSend(
        "/sub/channels." + channelId + ".messages",
        messageDto
    );
  }
}