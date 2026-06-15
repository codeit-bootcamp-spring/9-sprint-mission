package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.entity.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

  private final SimpMessagingTemplate template;
  private final BasicMessageService messageService;
  private final ApplicationEventPublisher eventPublisher;

  @MessageMapping("/messages")
  public void sendMessage(MessageCreateRequest request) {
    MessageDto sendMessage = messageService.create(request, null);
    MessageCreatedEvent event = MessageCreatedEvent.from(sendMessage);
    eventPublisher.publishEvent(event);
    template.convertAndSend("/sub/messages", sendMessage);
  }

}
