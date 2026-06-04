package com.sprint.mission.discodeit.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NotificationRequiredEventListenerTest {

  @Mock
  private ReadStatusRepository readStatusRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private NotificationRepository notificationRepository;

  @InjectMocks
  private NotificationRequiredEventListener listener;

  @Test
  @DisplayName("on MessageCreatedEvent 성공: 알림 활성 사용자에게만 알림을 생성하고 작성자는 제외한다")
  void onMessageCreated_success() {
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

    given(readStatusRepository.findAllNotificationEnabledByChannelIdWithUser(channelId))
        .willReturn(List.of(authorReadStatus, receiverReadStatus));

    listener.on(new MessageCreatedEvent(
        UUID.randomUUID(),
        channelId,
        "general",
        authorId,
        "author",
        "hello"
    ));

    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<Notification>> captor = ArgumentCaptor.forClass(List.class);
    then(notificationRepository).should().saveAll(captor.capture());
    List<Notification> notifications = captor.getValue();

    assertEquals(1, notifications.size());
    assertEquals(receiver, notifications.get(0).getReceiver());
    assertEquals("author (#general)", notifications.get(0).getTitle());
    assertEquals("hello", notifications.get(0).getContent());
  }

  @Test
  @DisplayName("on RoleUpdatedEvent 성공: 권한 변경 당사자에게 알림을 생성한다")
  void onRoleUpdated_success() {
    UUID userId = UUID.randomUUID();
    User user = new User("jun", "jun@test.com", "pw", null);

    given(userRepository.findById(userId)).willReturn(Optional.of(user));

    listener.on(new RoleUpdatedEvent(userId, UserRole.USER, UserRole.CHANNEL_MANAGER));

    ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
    then(notificationRepository).should().save(captor.capture());
    Notification notification = captor.getValue();

    assertEquals(user, notification.getReceiver());
    assertEquals("권한이 변경되었습니다.", notification.getTitle());
    assertEquals("USER -> CHANNEL_MANAGER", notification.getContent());
    then(readStatusRepository).shouldHaveNoInteractions();
    then(notificationRepository).should().save(any(Notification.class));
  }
}
