package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

  private final BasicMessageService messageService;

  @MessageMapping("/messages")
  public void sendMessage(MessageCreateRequest request) {
    // 메시지 생성 자체(BasicMessageService.create)가 트랜잭션 커밋 후
    // MessageCreatedEvent를 발행하고, WebSocketRequiredEventListener가
    // "/sub/channels.{channelId}.messages" 로 브로드캐스트합니다.
    // 여기서 별도로 이벤트를 재발행하거나 다른 토픽으로 브로드캐스트하지 않습니다
    // (프론트가 구독하지 않는 죽은 토픽으로 중복 발송하던 코드를 제거함).
    messageService.create(request, null);
  }

}
