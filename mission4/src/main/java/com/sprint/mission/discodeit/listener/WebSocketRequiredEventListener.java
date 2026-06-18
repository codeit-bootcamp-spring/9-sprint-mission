package com.sprint.mission.discodeit.listener;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class WebSocketRequiredEventListener {

  private final SimpMessagingTemplate template;
  private final BasicMessageService messageService;

  public void handleMessage(MessageCreatedEvent event) {
    MessageDto dto = messageService.find(event.messageId());
    template.convertAndSend("/sub/channels." + event.channelId() + ".messages", dto);
  }


}
