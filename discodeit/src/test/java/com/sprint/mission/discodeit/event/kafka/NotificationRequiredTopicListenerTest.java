package com.sprint.mission.discodeit.event.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserRole;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentUploadFailureNotifier;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NotificationRequiredTopicListenerTest {

  @Mock
  private ReadStatusRepository readStatusRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private NotificationRepository notificationRepository;
  @Mock
  private BinaryContentUploadFailureNotifier failureNotifier;

  private final ObjectMapper objectMapper = new ObjectMapper();

  private NotificationRequiredTopicListener listener;

  @BeforeEach
  void setUp() {
    listener = new NotificationRequiredTopicListener(
        readStatusRepository,
        userRepository,
        notificationRepository,
        failureNotifier,
        objectMapper
    );
  }

  @Test
  @DisplayName("MessageCreatedEvent topic 소비: 알림 활성 사용자에게 알림을 생성하고 작성자는 제외한다")
  void onMessageCreatedEvent_createsNotifications() throws Exception {
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

    listener.onMessageCreatedEvent(objectMapper.writeValueAsString(event));

    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<Notification>> captor = ArgumentCaptor.forClass(List.class);
    then(notificationRepository).should().saveAll(captor.capture());
    List<Notification> notifications = captor.getValue();

    assertThat(notifications).hasSize(1);
    assertThat(notifications.get(0).getReceiver()).isEqualTo(receiver);
    assertThat(notifications.get(0).getTitle()).isEqualTo("author (#general)");
    assertThat(notifications.get(0).getContent()).isEqualTo("hello");
  }

  @Test
  @DisplayName("RoleUpdatedEvent topic 소비: 권한 변경 당사자에게 알림을 생성한다")
  void onRoleUpdatedEvent_createsNotification() throws Exception {
    UUID userId = UUID.randomUUID();
    User user = new User("jun", "jun@test.com", "pw", null);
    RoleUpdatedEvent event = new RoleUpdatedEvent(userId, UserRole.USER, UserRole.ADMIN);

    given(userRepository.findById(userId)).willReturn(Optional.of(user));

    listener.onRoleUpdatedEvent(objectMapper.writeValueAsString(event));

    ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
    then(notificationRepository).should().save(captor.capture());
    assertThat(captor.getValue().getReceiver()).isEqualTo(user);
    assertThat(captor.getValue().getTitle()).isEqualTo("권한이 변경되었습니다.");
    assertThat(captor.getValue().getContent()).isEqualTo("USER -> ADMIN");
  }

  @Test
  @DisplayName("S3UploadFailedEvent topic 소비: 관리자 실패 알림 생성을 위임한다")
  void onS3UploadFailedEvent_delegatesFailureNotification() throws Exception {
    S3UploadFailedEvent event = new S3UploadFailedEvent(
        "S3 binary content upload",
        "request-123",
        UUID.randomUUID(),
        "S3 access denied"
    );

    listener.onS3UploadFailedEvent(objectMapper.writeValueAsString(event));

    then(failureNotifier).should().notifyAdmins(any(S3UploadFailedEvent.class));
  }
}
