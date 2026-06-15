package com.sprint.mission.discodeit.controller.websocket;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

  private final MessageService messageService;

  @MessageMapping("/messages")
  public void createMessage(MessageCreateRequest request) {
    log.info("웹소켓 메시지 생성 요청: request={}", request);
    MessageDto createdMessage = messageService.create(request, new ArrayList<>());
    log.debug("웹소켓 메시지 생성 응답: {}", createdMessage);
  }
}
