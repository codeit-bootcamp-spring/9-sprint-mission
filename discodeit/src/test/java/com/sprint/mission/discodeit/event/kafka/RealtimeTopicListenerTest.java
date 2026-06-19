package com.sprint.mission.discodeit.event.kafka;

import static org.mockito.BDDMockito.then;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.response.NotificationDto;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.SseBinaryContentUpdatedEvent;
import com.sprint.mission.discodeit.event.SseNotificationCreatedEvent;
import com.sprint.mission.discodeit.service.SseService;
import com.sprint.mission.discodeit.sse.SseEventNames;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@ExtendWith(MockitoExtension.class)
class RealtimeTopicListenerTest {

  @Mock
  private SimpMessagingTemplate messagingTemplate;

  @Mock
  private SseService sseService;

  private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

  private RealtimeTopicListener listener;

  @BeforeEach
  void setUp() {
    listener = new RealtimeTopicListener(messagingTemplate, sseService, objectMapper);
  }

  @Test
  @DisplayName("실시간 메시지 topic 소비: 채널 구독 destination으로 WebSocket 전송한다")
  void onMessageCreatedEvent_sendsWebSocketMessage() throws Exception {
    UUID channelId = UUID.randomUUID();
    MessageCreatedEvent event = new MessageCreatedEvent(
        UUID.randomUUID(),
        channelId,
        "general",
        UUID.randomUUID(),
        "author",
        "hello"
    );

    listener.onMessageCreatedEvent(objectMapper.writeValueAsString(event));

    then(messagingTemplate).should()
        .convertAndSend("/sub/channels.%s.messages".formatted(channelId), event);
  }

  @Test
  @DisplayName("SSE 알림 topic 소비: 대상 사용자에게만 전송한다")
  void onSseNotificationCreatedEvent_sendsToReceiver() throws Exception {
    UUID receiverId = UUID.randomUUID();
    NotificationDto notification = new NotificationDto(
        UUID.randomUUID(),
        Instant.now(),
        receiverId,
        "title",
        "content"
    );
    SseNotificationCreatedEvent event = new SseNotificationCreatedEvent(receiverId, notification);

    listener.onSseNotificationCreatedEvent(objectMapper.writeValueAsString(event));

    then(sseService).should()
        .send(List.of(receiverId), SseEventNames.NOTIFICATIONS_CREATED, notification);
  }

  @Test
  @DisplayName("SSE 파일 상태 topic 소비: 전체 연결에 브로드캐스트한다")
  void onSseBinaryContentUpdatedEvent_broadcasts() throws Exception {
    BinaryContentResponse binaryContent = new BinaryContentResponse(
        UUID.randomUUID(),
        "a.png",
        3L,
        "image/png",
        BinaryContentStatus.SUCCESS
    );
    SseBinaryContentUpdatedEvent event = new SseBinaryContentUpdatedEvent(binaryContent);

    listener.onSseBinaryContentUpdatedEvent(objectMapper.writeValueAsString(event));

    then(sseService).should().broadcast(SseEventNames.BINARY_CONTENTS_UPDATED, binaryContent);
  }
}
