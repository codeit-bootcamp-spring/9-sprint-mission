package com.sprint.mission.discodeit.event;

import static org.mockito.BDDMockito.then;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@ExtendWith(MockitoExtension.class)
class WebSocketRequiredEventListenerTest {

  @Mock
  private SimpMessagingTemplate messagingTemplate;

  @InjectMocks
  private WebSocketRequiredEventListener listener;

  @Test
  @DisplayName("MessageCreatedEvent를 채널 메시지 구독 destination으로 전송한다")
  void handleMessage_sendsMessageEventToChannelSubscription() {
    UUID channelId = UUID.randomUUID();
    MessageCreatedEvent event = new MessageCreatedEvent(
        UUID.randomUUID(),
        channelId,
        "general",
        UUID.randomUUID(),
        "author",
        "hello"
    );

    listener.handleMessage(event);

    then(messagingTemplate).should()
        .convertAndSend("/sub/channels.%s.messages".formatted(channelId), event);
  }
}
