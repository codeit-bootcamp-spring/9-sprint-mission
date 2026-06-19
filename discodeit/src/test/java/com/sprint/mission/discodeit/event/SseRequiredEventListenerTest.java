package com.sprint.mission.discodeit.event;

import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.NotificationDto;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.SseService;
import com.sprint.mission.discodeit.sse.SseEventNames;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SseRequiredEventListenerTest {

  @Mock
  private SseService sseService;

  @InjectMocks
  private SseRequiredEventListener listener;

  @Test
  @DisplayName("알림 생성 이벤트는 대상 사용자에게만 전송한다")
  void onNotificationCreated_sendsToReceiver() {
    UUID receiverId = UUID.randomUUID();
    NotificationDto notification = new NotificationDto(
        UUID.randomUUID(),
        Instant.now(),
        receiverId,
        "title",
        "content"
    );

    listener.on(new SseNotificationCreatedEvent(receiverId, notification));

    then(sseService).should()
        .send(List.of(receiverId), SseEventNames.NOTIFICATIONS_CREATED, notification);
  }

  @Test
  @DisplayName("파일 상태 변경 이벤트는 브로드캐스트한다")
  void onBinaryContentUpdated_broadcasts() {
    BinaryContentResponse binaryContent = new BinaryContentResponse(
        UUID.randomUUID(),
        "a.png",
        3L,
        "image/png",
        BinaryContentStatus.SUCCESS
    );

    listener.on(new SseBinaryContentUpdatedEvent(binaryContent));

    then(sseService).should().broadcast(SseEventNames.BINARY_CONTENTS_UPDATED, binaryContent);
  }

  @Test
  @DisplayName("비공개 채널 이벤트는 지정된 사용자에게만 전송한다")
  void onPrivateChannelChanged_sendsToReceivers() {
    UUID receiverId = UUID.randomUUID();
    ChannelResponse channel = new ChannelResponse(
        UUID.randomUUID(),
        ChannelType.PRIVATE,
        "private",
        null,
        List.of(),
        Instant.now()
    );

    listener.on(new SseChannelChangedEvent(
        SseEventNames.CHANNELS_CREATED,
        channel,
        List.of(receiverId)
    ));

    then(sseService).should().send(List.of(receiverId), SseEventNames.CHANNELS_CREATED, channel);
  }

  @Test
  @DisplayName("사용자 변경 이벤트는 브로드캐스트한다")
  void onUserChanged_broadcasts() {
    UserResponse user = new UserResponse(UUID.randomUUID(), "jun", "jun@test.com", null, true);

    listener.on(new SseUserChangedEvent(SseEventNames.USERS_UPDATED, user));

    then(sseService).should().broadcast(SseEventNames.USERS_UPDATED, user);
  }
}
