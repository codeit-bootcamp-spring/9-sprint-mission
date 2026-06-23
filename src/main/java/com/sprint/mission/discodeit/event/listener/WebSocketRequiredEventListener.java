package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.event.message.MessageCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketRequiredEventListener {

  private final SimpMessagingTemplate messagingTemplate;

  @TransactionalEventListener
  public void handleMessage(MessageCreatedEvent event) {

    MessageDto message = event.getData();

    String destination =
        "/sub/channels."
            + message.channelId()
            + ".messages";

    log.info(
        "웹소켓 메시지 전송: destination={}, messageId={}",
        destination,
        message.id()
    );

    messagingTemplate.convertAndSend(
        destination,
        message
    );
  }
}