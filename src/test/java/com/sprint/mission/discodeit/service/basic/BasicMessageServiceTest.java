package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.sprint.mission.discodeit.dto.data.MessageDto;
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
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

@ExtendWith(MockitoExtension.class)
class BasicMessageServiceTest {

  @Mock MessageRepository messageRepository;
  @Mock ChannelRepository channelRepository;
  @Mock UserRepository userRepository;
  @Mock MessageMapper messageMapper;
  @Mock BinaryContentStorage binaryContentStorage;
  @Mock BinaryContentRepository binaryContentRepository;
  @Mock PageResponseMapper pageResponseMapper;

  @InjectMocks BasicMessageService messageService;

  @Test
  void createMessage_success() {
    UUID channelId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    MessageCreateRequest request =
        new MessageCreateRequest("hello", channelId, userId);

    given(channelRepository.findById(channelId))
        .willReturn(Optional.of(mock(Channel.class)));
    given(userRepository.findById(userId))
        .willReturn(Optional.of(mock(User.class)));

    given(messageMapper.toDto(any()))
        .willReturn(mock(MessageDto.class));

    assertDoesNotThrow(() ->
        messageService.create(request, List.of()));
  }

  @Test
  void createMessage_fail_channelNotFound() {
    UUID channelId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    MessageCreateRequest request =
        new MessageCreateRequest("hello", channelId, userId);

    given(channelRepository.findById(channelId)).willReturn(Optional.empty());

    assertThrows(ChannelNotFoundException.class,
        () -> messageService.create(request, List.of()));
  }

  @Test
  void createMessage_fail_userNotFound() {
    UUID channelId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    MessageCreateRequest request =
        new MessageCreateRequest("hello", channelId, userId);

    given(channelRepository.findById(channelId))
        .willReturn(Optional.of(mock(Channel.class)));
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    assertThrows(UserNotFoundException.class,
        () -> messageService.create(request, List.of()));
  }

  @Test
  void createMessage_withAttachments() {
    UUID channelId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    MessageCreateRequest request =
        new MessageCreateRequest("hello", channelId, userId);

    given(channelRepository.findById(channelId))
        .willReturn(Optional.of(mock(Channel.class)));
    given(userRepository.findById(userId))
        .willReturn(Optional.of(mock(User.class)));

    given(messageMapper.toDto(any()))
        .willReturn(mock(MessageDto.class));

    var file = mock(com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest.class);
    given(file.fileName()).willReturn("test.png");
    given(file.contentType()).willReturn("image/png");
    given(file.bytes()).willReturn(new byte[]{1,2,3});

    assertDoesNotThrow(() ->
        messageService.create(request, List.of(file)));

    then(binaryContentRepository).should().save(any());
    then(binaryContentStorage).should().put(any(), any());
  }

  @Test
  void updateMessage_success() {
    UUID id = UUID.randomUUID();
    Message message = mock(Message.class);

    given(messageRepository.findById(id)).willReturn(Optional.of(message));
    given(messageMapper.toDto(any())).willReturn(mock(MessageDto.class));

    assertDoesNotThrow(() ->
        messageService.update(id, mock(MessageUpdateRequest.class)));
  }

  @Test
  void updateMessage_fail_messageNotFound() {
    UUID id = UUID.randomUUID();

    given(messageRepository.findById(id)).willReturn(Optional.empty());

    assertThrows(MessageNotFoundException.class,
        () -> messageService.update(id, mock(MessageUpdateRequest.class)));
  }

  @Test
  void deleteMessage_success() {
    UUID id = UUID.randomUUID();

    given(messageRepository.existsById(id)).willReturn(true);

    messageService.delete(id);

    then(messageRepository).should().deleteById(id);
  }

  @Test
  void deleteMessage_fail_messageNotFound() {
    UUID id = UUID.randomUUID();

    given(messageRepository.existsById(id)).willReturn(false);

    assertThrows(MessageNotFoundException.class,
        () -> messageService.delete(id));
  }

  @Test
  void findMessage_success() {
    UUID id = UUID.randomUUID();

    given(messageRepository.findById(id))
        .willReturn(Optional.of(mock(Message.class)));
    given(messageMapper.toDto(any()))
        .willReturn(mock(MessageDto.class));

    assertDoesNotThrow(() -> messageService.find(id));
  }

  @Test
  void findMessage_fail() {
    UUID id = UUID.randomUUID();

    given(messageRepository.findById(id)).willReturn(Optional.empty());

    assertThrows(MessageNotFoundException.class,
        () -> messageService.find(id));
  }

  @Test
  @SuppressWarnings("unchecked")
  void findAllByChannelId_success() {
    UUID channelId = UUID.randomUUID();

    Slice<Message> slice = (Slice<Message>) mock(Slice.class);

    given(messageRepository.findAllByChannelIdWithAuthor(any(), any(), any()))
        .willReturn(slice);

    given(slice.map(any())).willAnswer(invocation -> {
      Slice<MessageDto> mappedSlice = (Slice<MessageDto>) mock(Slice.class);
      given(mappedSlice.getContent()).willReturn(List.of());
      return mappedSlice;
    });

    given(pageResponseMapper.fromSlice(any(), any()))
        .willReturn(mock(PageResponse.class));

    PageResponse<MessageDto> response =
        messageService.findAllByChannelId(channelId, null, mock(Pageable.class));

    assertNotNull(response);
    then(pageResponseMapper).should().fromSlice(any(), any());
  }
}