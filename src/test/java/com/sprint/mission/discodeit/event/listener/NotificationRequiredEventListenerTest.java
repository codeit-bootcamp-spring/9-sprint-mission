package com.sprint.mission.discodeit.event.listener;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.message.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.message.RoleUpdatedEvent;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.NotificationService;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NotificationRequiredEventListenerTest {

  @Mock
  private NotificationService notificationService;

  @Mock
  private ReadStatusRepository readStatusRepository;

  @Mock
  private ChannelService channelService;

  @InjectMocks
  private NotificationRequiredEventListener eventListener;

  private UUID authorId;
  private UUID channelId;
  private UUID privateChannelId;
  private UUID receiverId1;
  private UUID receiverId2;
  private MessageDto messageDto;
  private ChannelDto publicChannelDto;
  private ChannelDto privateChannelDto;
  private UserDto authorDto;

  @BeforeEach
  void setUp() {
    authorId = UUID.randomUUID();
    channelId = UUID.randomUUID();
    privateChannelId = UUID.randomUUID();
    receiverId1 = UUID.randomUUID();
    receiverId2 = UUID.randomUUID();

    authorDto = new UserDto(
        authorId,
        "author",
        "author@example.com",
        null,
        true,
        Role.USER
    );

    messageDto = new MessageDto(
        UUID.randomUUID(),
        Instant.now(),
        Instant.now(),
        "Hello world",
        channelId,
        authorDto,
        List.of()
    );

    publicChannelDto = new ChannelDto(
        channelId,
        ChannelType.PUBLIC,
        "general",
        "General chat",
        List.of(),
        Instant.now()
    );

    privateChannelDto = new ChannelDto(
        privateChannelId,
        ChannelType.PRIVATE,
        null,
        null,
        List.of(),
        Instant.now()
    );
  }

  @Test
  @DisplayName("PublicChannel에서 메시지 생성 시 알림 생성")
  void onMessageCreated_PublicChannel_CreatesNotifications() {
    // given
    MessageCreatedEvent event = new MessageCreatedEvent(messageDto, messageDto.createdAt());

    User user1 = createMockUser(receiverId1);
    User user2 = createMockUser(receiverId2);

    ReadStatus readStatus1 = createMockReadStatus(user1, true);
    ReadStatus readStatus2 = createMockReadStatus(user2, true);

    given(channelService.find(channelId)).willReturn(publicChannelDto);
    given(readStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(channelId))
        .willReturn(List.of(readStatus1, readStatus2));

    String expectedTitle = "author (#general)";
    String expectedContent = "Hello world";

    // when
    eventListener.on(event);

    // then
    verify(notificationService).create(
        eq(Set.of(receiverId1, receiverId2)),
        eq(expectedTitle),
        eq(expectedContent)
    );
  }

  @Test
  @DisplayName("PrivateChannel에서 메시지 생성 시 알림 생성")
  void onMessageCreated_PrivateChannel_CreatesNotifications() {
    // given
    MessageCreatedEvent event = new MessageCreatedEvent(messageDto, messageDto.createdAt());

    User user1 = createMockUser(receiverId1);
    ReadStatus readStatus1 = createMockReadStatus(user1, true);

    given(channelService.find(channelId)).willReturn(privateChannelDto);
    given(readStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(channelId))
        .willReturn(List.of(readStatus1));

    String expectedTitle = "author"; // No channel name for private channels
    String expectedContent = "Hello world";

    // when
    eventListener.on(event);

    // then
    verify(notificationService).create(
        eq(Set.of(receiverId1)),
        eq(expectedTitle),
        eq(expectedContent)
    );
  }

  @Test
  @DisplayName("메시지 작성자는 알림 수신자에서 제외")
  void onMessageCreated_ExcludesAuthorFromReceivers() {
    // given
    MessageCreatedEvent event = new MessageCreatedEvent(messageDto, messageDto.createdAt());

    User authorUser = createMockUser(authorId);
    User receiverUser = createMockUser(receiverId1);

    ReadStatus authorReadStatus = createMockReadStatus(authorUser, true);
    ReadStatus receiverReadStatus = createMockReadStatus(receiverUser, true);

    given(channelService.find(channelId)).willReturn(publicChannelDto);
    given(readStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(channelId))
        .willReturn(List.of(authorReadStatus, receiverReadStatus));

    // when
    eventListener.on(event);

    // then
    verify(notificationService).create(
        eq(Set.of(receiverId1)), // Only receiver, not author
        any(String.class),
        any(String.class)
    );
  }

  @Test
  @DisplayName("알림이 비활성화된 사용자는 알림 수신자에서 제외")
  void onMessageCreated_ExcludesDisabledNotificationUsers() {
    // given
    MessageCreatedEvent event = new MessageCreatedEvent(messageDto, messageDto.createdAt());

    // Test scenario: repository returns empty list for users with notifications enabled

    given(channelService.find(channelId)).willReturn(publicChannelDto);
    given(readStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(channelId))
        .willReturn(List.of()); // No users with notifications enabled

    // when
    eventListener.on(event);

    // then
    verify(notificationService).create(eq(Set.of()), any(String.class), any(String.class));
  }

  @Test
  @DisplayName("역할 변경 이벤트 시 알림 생성")
  void onRoleUpdated_CreatesNotification() {
    // given
    UUID userId = UUID.randomUUID();
    Role fromRole = Role.USER;
    Role toRole = Role.ADMIN;
    Instant updatedAt = Instant.now();

    RoleUpdatedEvent event = new RoleUpdatedEvent(userId, fromRole, toRole, updatedAt);

    String expectedTitle = "권한이 변경되었습니다.";
    String expectedContent = "USER -> ADMIN";

    // when
    eventListener.on(event);

    // then
    verify(notificationService).create(
        eq(Set.of(userId)),
        eq(expectedTitle),
        eq(expectedContent)
    );
  }

  @Test
  @DisplayName("수신자가 없을 때는 알림을 생성하지 않음")
  void onMessageCreated_NoReceivers_NoNotificationCreated() {
    // given
    MessageCreatedEvent event = new MessageCreatedEvent(messageDto, messageDto.createdAt());

    given(channelService.find(channelId)).willReturn(publicChannelDto);
    given(readStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(channelId))
        .willReturn(List.of());

    // when
    eventListener.on(event);

    // then
    verify(notificationService).create(eq(Set.of()), any(String.class), any(String.class));
  }

  private User createMockUser(UUID userId) {
    User user = new User("user", "user@example.com", "password", null);
    ReflectionTestUtils.setField(user, "id", userId);
    return user;
  }

  private ReadStatus createMockReadStatus(User user, boolean notificationEnabled) {
    Channel mockChannel = new Channel(ChannelType.PUBLIC, "test", "test");
    ReadStatus readStatus = new ReadStatus(user, mockChannel, Instant.now());
    ReflectionTestUtils.setField(readStatus, "notificationEnabled", notificationEnabled);
    return readStatus;
  }
}