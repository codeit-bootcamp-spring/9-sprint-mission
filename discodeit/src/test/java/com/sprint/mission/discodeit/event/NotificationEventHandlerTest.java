package com.sprint.mission.discodeit.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserRole;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentUploadFailureNotifier;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NotificationEventHandlerTest {

  @Mock
  private ReadStatusRepository readStatusRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private NotificationRepository notificationRepository;
  @Mock
  private BinaryContentUploadFailureNotifier failureNotifier;
  @Mock
  private NotificationCacheEvictor cacheEvictor;

  @Test
  @DisplayName("MessageCreatedEvent 처리: 알림 활성 사용자에게 알림을 생성하고 작성자는 제외한다")
  void handleMessageCreated_createsNotifications() {
    NotificationEventHandler handler = newHandler();
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    UUID receiverId = UUID.randomUUID();
    Channel channel = new Channel(ChannelType.PUBLIC, "general", "desc");
    User author = new User("author", "author@test.com", "pw", null);
    User receiver = new User("receiver", "receiver@test.com", "pw", null);
    ReflectionTestUtils.setField(author, "id", authorId);
    ReflectionTestUtils.setField(receiver, "id", receiverId);
    ReadStatus authorReadStatus = new ReadStatus(author, channel, Instant.now());
    ReadStatus receiverReadStatus = new ReadStatus(receiver, channel, Instant.now());
    MessageCreatedEvent event = new MessageCreatedEvent(
        UUID.randomUUID(),
        channelId,
        "general",
        authorId,
        "author",
        "hello"
    );

    given(readStatusRepository.findAllNotificationEnabledByChannelIdWithUser(channelId))
        .willReturn(List.of(authorReadStatus, receiverReadStatus));
    given(notificationRepository.existsByReceiverIdAndEventKey(
        receiverId,
        "message-created:" + event.messageId()
    )).willReturn(false);

    handler.handle(event);

    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<Notification>> captor = ArgumentCaptor.forClass(List.class);
    then(notificationRepository).should().saveAll(captor.capture());
    List<Notification> notifications = captor.getValue();

    assertThat(notifications).hasSize(1);
    assertThat(notifications.get(0).getReceiver()).isEqualTo(receiver);
    assertThat(notifications.get(0).getTitle()).isEqualTo("author (#general)");
    assertThat(notifications.get(0).getContent()).isEqualTo("hello");
    assertThat(notifications.get(0).getEventKey()).isEqualTo("message-created:" + event.messageId());
    then(cacheEvictor).should().evictReceivers(List.of(receiverId));
  }

  @Test
  @DisplayName("MessageCreatedEvent 처리: 동일 이벤트 키가 있으면 중복 알림을 만들지 않는다")
  void handleMessageCreated_skipsDuplicateEvent() {
    NotificationEventHandler handler = newHandler();
    UUID channelId = UUID.randomUUID();
    UUID receiverId = UUID.randomUUID();
    User receiver = new User("receiver", "receiver@test.com", "pw", null);
    ReflectionTestUtils.setField(receiver, "id", receiverId);
    ReadStatus receiverReadStatus = new ReadStatus(
        receiver,
        new Channel(ChannelType.PUBLIC, "general", "desc"),
        Instant.now()
    );
    MessageCreatedEvent event = new MessageCreatedEvent(
        UUID.randomUUID(),
        channelId,
        "general",
        UUID.randomUUID(),
        "author",
        "hello"
    );

    given(readStatusRepository.findAllNotificationEnabledByChannelIdWithUser(channelId))
        .willReturn(List.of(receiverReadStatus));
    given(notificationRepository.existsByReceiverIdAndEventKey(
        receiverId,
        "message-created:" + event.messageId()
    )).willReturn(true);

    handler.handle(event);

    then(notificationRepository).should().saveAll(List.of());
    then(cacheEvictor).should().evictReceivers(List.of());
  }

  @Test
  @DisplayName("RoleUpdatedEvent 처리: 권한 변경 당사자에게 알림을 생성한다")
  void handleRoleUpdated_createsNotification() {
    NotificationEventHandler handler = newHandler();
    UUID userId = UUID.randomUUID();
    User user = new User("jun", "jun@test.com", "pw", null);
    ReflectionTestUtils.setField(user, "id", userId);
    RoleUpdatedEvent event = new RoleUpdatedEvent(userId, UserRole.USER, UserRole.ADMIN);
    String eventKey = "role-updated:%s:%s:%s".formatted(userId, UserRole.USER, UserRole.ADMIN);

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(notificationRepository.existsByReceiverIdAndEventKey(userId, eventKey))
        .willReturn(false);

    handler.handle(event);

    ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
    then(notificationRepository).should().save(captor.capture());
    assertThat(captor.getValue().getReceiver()).isEqualTo(user);
    assertThat(captor.getValue().getTitle()).isEqualTo("권한이 변경되었습니다.");
    assertThat(captor.getValue().getContent()).isEqualTo("USER -> ADMIN");
    assertThat(captor.getValue().getEventKey()).isEqualTo(eventKey);
    then(cacheEvictor).should().evictReceiver(userId);
  }

  @Test
  @DisplayName("S3UploadFailedEvent 처리: 이벤트 키와 함께 관리자 알림을 위임한다")
  void handleS3UploadFailed_delegatesFailureNotification() {
    NotificationEventHandler handler = newHandler();
    UUID binaryContentId = UUID.randomUUID();
    S3UploadFailedEvent event = new S3UploadFailedEvent(
        "S3 binary content upload",
        "request-123",
        binaryContentId,
        "S3 access denied"
    );

    handler.handle(event);

    then(failureNotifier).should()
        .notifyAdmins(event, "s3-upload-failed:" + binaryContentId);
  }

  private NotificationEventHandler newHandler() {
    return new NotificationEventHandler(
        readStatusRepository,
        userRepository,
        notificationRepository,
        failureNotifier,
        cacheEvictor
    );
  }
}
