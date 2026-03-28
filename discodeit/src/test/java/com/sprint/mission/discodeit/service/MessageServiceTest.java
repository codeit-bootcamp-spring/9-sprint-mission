package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

  @Mock private MessageRepository messageRepository;
  @Mock private ChannelRepository channelRepository;
  @Mock private UserRepository userRepository;
  @Mock private MessageMapper messageMapper;
  @Mock private BinaryContentStorage binaryContentStorage;
  @Mock private BinaryContentRepository binaryContentRepository;
  @Mock private PageResponseMapper pageResponseMapper;

  @InjectMocks
  private BasicMessageService messageService;


  @Test
  void create_success() {

    UUID channelId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    MessageCreateRequest request =
        new MessageCreateRequest("content", channelId, userId);

    Channel channel = mock(Channel.class);
    User user = mock(User.class);
    Message message = mock(Message.class);

    MessageDto dto = new MessageDto(
        UUID.randomUUID(),
        Instant.now(),
        null,
        "content",
        channelId,
        new UserDto(userId, "user", "email", null, false),
        List.of()
    );

    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(messageRepository.save(any(Message.class))).willReturn(message);
    given(messageMapper.toDto(any(Message.class))).willReturn(dto);

    MessageDto result = messageService.create(request, List.of());

    assertThat(result.content()).isEqualTo("content");
  }

  @Test
  void create_fail_channel_not_found() {
    UUID channelId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    MessageCreateRequest request =
        new MessageCreateRequest("content", channelId, userId);

    given(channelRepository.findById(channelId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.create(request, List.of()))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  @Test
  void create_fail_user_not_found() {
    UUID channelId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    MessageCreateRequest request =
        new MessageCreateRequest("content", channelId, userId);

    given(channelRepository.findById(channelId))
        .willReturn(Optional.of(mock(Channel.class)));
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.create(request, List.of()))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  void find_success() {
    UUID id = UUID.randomUUID();

    Message message = mock(Message.class);

    MessageDto dto = new MessageDto(
        id,
        Instant.now(),
        null,
        "content",
        UUID.randomUUID(),
        null,
        List.of()
    );

    given(messageRepository.findById(id)).willReturn(Optional.of(message));
    given(messageMapper.toDto(message)).willReturn(dto);

    MessageDto result = messageService.find(id);

    assertThat(result.id()).isEqualTo(id);
  }

  @Test
  void find_fail_not_found() {
    UUID id = UUID.randomUUID();

    given(messageRepository.findById(id)).willReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.find(id))
        .isInstanceOf(MessageNotFoundException.class);
  }

  @Test
  void update_success() {
    UUID id = UUID.randomUUID();

    Message message = mock(Message.class);

    MessageUpdateRequest request = new MessageUpdateRequest("new");

    MessageDto dto = new MessageDto(
        id,
        Instant.now(),
        null,
        "new",
        UUID.randomUUID(),
        null,
        List.of()
    );

    given(messageRepository.findById(id)).willReturn(Optional.of(message));
    given(messageMapper.toDto(message)).willReturn(dto);

    MessageDto result = messageService.update(id, request);

    assertThat(result.content()).isEqualTo("new");
  }

  @Test
  void update_fail_not_found() {
    UUID id = UUID.randomUUID();

    given(messageRepository.findById(id)).willReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.update(id,
        new MessageUpdateRequest("new")))
        .isInstanceOf(MessageNotFoundException.class);
  }

  @Test
  void delete_success() {
    UUID id = UUID.randomUUID();

    given(messageRepository.existsById(id)).willReturn(true);

    messageService.delete(id);

    then(messageRepository).should().deleteById(id);
  }

  @Test
  void delete_fail_not_found() {
    UUID id = UUID.randomUUID();

    given(messageRepository.existsById(id)).willReturn(false);

    assertThatThrownBy(() -> messageService.delete(id))
        .isInstanceOf(MessageNotFoundException.class);
  }

  @SuppressWarnings("unchecked")
  @Test
  void findAllByChannelId_success() {
    UUID channelId = UUID.randomUUID();

    MessageDto dto = new MessageDto(
        UUID.randomUUID(),
        Instant.now(),
        null,
        "content",
        channelId,
        null,
        List.of()
    );

    Slice<MessageDto> slice = new SliceImpl<>(List.of(dto));

    given(messageRepository.findAllByChannelIdWithAuthor(
        any(), any(), any()))
        .willReturn(slice.map(m -> mock(Message.class)));

    given(messageMapper.toDto(any())).willReturn(dto);

    PageResponse<MessageDto> pageResponse = mock(PageResponse.class);

    doReturn(pageResponse)
        .when(pageResponseMapper)
        .fromSlice(any(), any());

    PageResponse<MessageDto> result =
        messageService.findAllByChannelId(channelId, null, Pageable.unpaged());

    assertThat(result).isNotNull();
  }
}