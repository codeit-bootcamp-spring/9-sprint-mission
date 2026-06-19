package com.sprint.mission.discodeit.event.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.UserRole;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.NotificationEventHandler;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationRequiredTopicListenerTest {

  @Mock
  private NotificationEventHandler notificationEventHandler;

  private final ObjectMapper objectMapper = new ObjectMapper();

  private NotificationRequiredTopicListener listener;

  @BeforeEach
  void setUp() {
    listener = new NotificationRequiredTopicListener(
        notificationEventHandler,
        objectMapper
    );
  }

  @Test
  @DisplayName("MessageCreatedEvent topic 소비: 이벤트를 역직렬화해 핸들러에 위임한다")
  void onMessageCreatedEvent_delegatesToHandler() throws Exception {
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    MessageCreatedEvent event = new MessageCreatedEvent(
        UUID.randomUUID(),
        channelId,
        "general",
        authorId,
        "author",
        "hello"
    );

    listener.onMessageCreatedEvent(objectMapper.writeValueAsString(event));

    ArgumentCaptor<MessageCreatedEvent> captor = ArgumentCaptor.forClass(MessageCreatedEvent.class);
    then(notificationEventHandler).should().handle(captor.capture());
    assertThat(captor.getValue()).isEqualTo(event);
  }

  @Test
  @DisplayName("RoleUpdatedEvent topic 소비: 이벤트를 역직렬화해 핸들러에 위임한다")
  void onRoleUpdatedEvent_delegatesToHandler() throws Exception {
    UUID userId = UUID.randomUUID();
    RoleUpdatedEvent event = new RoleUpdatedEvent(userId, UserRole.USER, UserRole.ADMIN);

    listener.onRoleUpdatedEvent(objectMapper.writeValueAsString(event));

    ArgumentCaptor<RoleUpdatedEvent> captor = ArgumentCaptor.forClass(RoleUpdatedEvent.class);
    then(notificationEventHandler).should().handle(captor.capture());
    assertThat(captor.getValue()).isEqualTo(event);
  }

  @Test
  @DisplayName("S3UploadFailedEvent topic 소비: 이벤트를 역직렬화해 핸들러에 위임한다")
  void onS3UploadFailedEvent_delegatesFailureNotification() throws Exception {
    S3UploadFailedEvent event = new S3UploadFailedEvent(
        "S3 binary content upload",
        "request-123",
        UUID.randomUUID(),
        "S3 access denied"
    );

    listener.onS3UploadFailedEvent(objectMapper.writeValueAsString(event));

    then(notificationEventHandler).should().handle(any(S3UploadFailedEvent.class));
  }
}
