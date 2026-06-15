package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "discodeit.kafka.enabled", havingValue = "false")
public class WebSocketRequiredEventListener {

  private final SimpMessagingTemplate messagingTemplate;
  private final MessageService messageService;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleMessage(MessageCreatedEvent event) {
    UUID channelId = event.channelId();
    MessageDto message = messageService.find(event.messageId());
    messagingTemplate.convertAndSend("/sub/channels." + channelId + ".messages", message);
  }
}
