package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.event.SseEventProducer;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;


@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {
    private final BasicMessageService messageService;
    private final SseEventProducer sseEventProducer;


    @MessageMapping("/messages")
    public void sendMessage(@Payload MessageCreateRequest request) {
        MessageDto result = messageService.create(request, null);
        sseEventProducer.broadcast("messages.created", result);
    }
}
