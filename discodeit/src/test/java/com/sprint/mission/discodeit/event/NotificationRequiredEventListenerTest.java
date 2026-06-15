package com.sprint.mission.discodeit.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.kafka.NotificationRequiredTopicListener;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NotificationRequiredEventListenerTest {

  @Mock
  private ReadStatusRepository readStatusRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private NotificationRepository notificationRepository;
  @Mock
  private CacheManager cacheManager;
  @Mock
  private NotificationMapper notificationMapper;
  @Mock
  private ApplicationEventPublisher eventPublisher;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void onMessageCreated_CreatesNotificationsExceptAuthor() {
    NotificationRequiredTopicListener listener =
        new NotificationRequiredTopicListener(readStatusRepository, userRepository,
            notificationRepository, cacheManager, objectMapper, notificationMapper, eventPublisher);
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    UUID receiverId = UUID.randomUUID();
    Channel channel = new Channel(ChannelType.PUBLIC, "general", "general channel");
    ReflectionTestUtils.setField(channel, "id", channelId);
    User author = user(authorId, "author");
    User receiver = user(receiverId, "receiver");
    ReadStatus authorReadStatus = new ReadStatus(author, channel, Instant.now());
    authorReadStatus.update(null, true);
    ReadStatus receiverReadStatus = new ReadStatus(receiver, channel, Instant.now());
    receiverReadStatus.update(null, true);
    given(readStatusRepository.findAllNotificationEnabledByChannelIdWithUser(channelId))
        .willReturn(List.of(authorReadStatus, receiverReadStatus));
    given(notificationRepository.saveAll(any()))
        .willAnswer(invocation -> invocation.getArgument(0));
    given(notificationMapper.toDto(any(Notification.class)))
        .willAnswer(invocation -> toDto(invocation.getArgument(0)));

    listener.onMessageCreatedEvent(toJson(new MessageCreatedEvent(
        UUID.randomUUID(),
        channelId,
        "general",
        authorId,
        "author",
        "hello"
    )));

    ArgumentCaptor<List<Notification>> captor = ArgumentCaptor.forClass(List.class);
    verify(notificationRepository).saveAll(captor.capture());
    assertThat(captor.getValue()).hasSize(1);
    assertThat(captor.getValue().get(0).getReceiver().getId()).isEqualTo(receiverId);
    assertThat(captor.getValue().get(0).getTitle()).isEqualTo("author (#general)");
    assertThat(captor.getValue().get(0).getContent()).isEqualTo("hello");
  }

  @Test
  void onRoleUpdated_CreatesNotificationForUpdatedUser() {
    NotificationRequiredTopicListener listener =
        new NotificationRequiredTopicListener(readStatusRepository, userRepository,
            notificationRepository, cacheManager, objectMapper, notificationMapper, eventPublisher);
    UUID userId = UUID.randomUUID();
    User user = user(userId, "receiver");
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(notificationRepository.save(any(Notification.class)))
        .willAnswer(invocation -> invocation.getArgument(0));
    given(notificationMapper.toDto(any(Notification.class)))
        .willAnswer(invocation -> toDto(invocation.getArgument(0)));

    listener.onRoleUpdatedEvent(toJson(new RoleUpdatedEvent(userId, Role.USER, Role.CHANNEL_MANAGER)));

    ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
    verify(notificationRepository).save(captor.capture());
    assertThat(captor.getValue().getReceiver().getId()).isEqualTo(userId);
    assertThat(captor.getValue().getContent()).isEqualTo("USER -> CHANNEL_MANAGER");
  }

  private User user(UUID id, String username) {
    User user = new User(username, username + "@example.com", "password", null);
    ReflectionTestUtils.setField(user, "id", id);
    return user;
  }

  private String toJson(Object event) {
    try {
      return objectMapper.writeValueAsString(event);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private NotificationDto toDto(Notification notification) {
    return new NotificationDto(
        notification.getId(),
        notification.getCreatedAt(),
        notification.getReceiver().getId(),
        notification.getTitle(),
        notification.getContent()
    );
  }
}
