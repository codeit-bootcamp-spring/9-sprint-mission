package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.List;
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
  public void sendMessage(
      MessageCreateRequest request
  ) {

    log.info("웹소켓 메시지 생성 요청: {}", request);

    messageService.create(
        request,
        List.of() // 첨부파일 없음
    );
  }
}