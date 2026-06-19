package com.sprint.mission.discodeit.event;

import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.entity.UserRole;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationRequiredEventListenerTest {

  @Mock
  private NotificationEventHandler notificationEventHandler;

  @InjectMocks
  private NotificationRequiredEventListener listener;

  @Test
  @DisplayName("MessageCreatedEvent 수신 시 알림 핸들러에 위임한다")
  void onMessageCreated_delegatesToHandler() {
    MessageCreatedEvent event = new MessageCreatedEvent(
        UUID.randomUUID(),
        UUID.randomUUID(),
        "general",
        UUID.randomUUID(),
        "author",
        "hello"
    );

    listener.on(event);

    then(notificationEventHandler).should().handle(event);
  }

  @Test
  @DisplayName("RoleUpdatedEvent 수신 시 알림 핸들러에 위임한다")
  void onRoleUpdated_delegatesToHandler() {
    RoleUpdatedEvent event = new RoleUpdatedEvent(
        UUID.randomUUID(),
        UserRole.USER,
        UserRole.CHANNEL_MANAGER
    );

    listener.on(event);

    then(notificationEventHandler).should().handle(event);
  }

  @Test
  @DisplayName("S3UploadFailedEvent 수신 시 알림 핸들러에 위임한다")
  void onS3UploadFailed_delegatesToHandler() {
    S3UploadFailedEvent event = new S3UploadFailedEvent(
        "S3 binary content upload",
        "request-123",
        UUID.randomUUID(),
        "S3 access denied"
    );

    listener.on(event);

    then(notificationEventHandler).should().handle(event);
  }
}
