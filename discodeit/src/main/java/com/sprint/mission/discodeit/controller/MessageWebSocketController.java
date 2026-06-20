package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class MessageWebSocketController {

  @MessageMapping("/messages")
  public void handleTextMessage(MessageCreateRequest request) {

  }

}
