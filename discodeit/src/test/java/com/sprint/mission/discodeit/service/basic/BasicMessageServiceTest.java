package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@ExtendWith(MockitoExtension.class)
class BasicMessageServiceTest {

  @Mock
  private MessageRepository messageRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private ReadStatusRepository readStatusRepository;
  @Mock
  private MessageMapper messageMapper;
  @Mock
  private PageResponseMapper pageResponseMapper;
  @Mock
  private BinaryContentService binaryContentService;

  @InjectMocks
  private BasicMessageService messageService;

  @Test
  @DisplayName("메시지 전송 성공 (자동 참여 포함)")
  void send_Success() {
    UUID authorId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    User author = new User("author", "a@t.com", "pw", null);
    Channel channel = new Channel("channel", "desc", ChannelType.PUBLIC, author);
    MessageCreateRequest request = new MessageCreateRequest("Hello", authorId, channelId);
    Message message = new Message("Hello", author, channel, List.of());

    given(userRepository.findById(authorId)).willReturn(Optional.of(author));
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(messageRepository.save(any(Message.class))).willReturn(message);
    given(messageMapper.toDto(any(Message.class))).willReturn(
        new MessageDto(UUID.randomUUID(), Instant.now(), null, "Hello", channelId,
            mock(UserDto.class), List.of()));

    MessageDto result = messageService.send(request, List.of());

    assertThat(result.content()).isEqualTo("Hello");
    verify(readStatusRepository).save(any());
  }

  @Test
  @DisplayName("메시지 목록 조회 성공")
  void findAllByChannelId_Success() {
    UUID channelId = UUID.randomUUID();
    Slice<Message> slice = new SliceImpl<>(List.of());
    PageResponse<MessageDto> mockResponse = new PageResponse<>(List.of(), null, 50, false, 0L);

    given(messageRepository.findMessagesNoOffset(any(), any(), any(Pageable.class))).willReturn(
        slice);
    given(pageResponseMapper.<MessageDto, Instant>fromSlice(any(), any())).willReturn(mockResponse);

    PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, null, 50);

    assertThat(result.size()).isEqualTo(50);
  }

  @Test
  @DisplayName("메시지 수정 성공")
  void update_Success() {
    UUID messageId = UUID.randomUUID();
    Message message = new Message("old", null, null, List.of());
    MessageUpdateRequest request = new MessageUpdateRequest("new");

    given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
    given(messageMapper.toDto(message)).willReturn(
        new MessageDto(messageId, Instant.now(), null, "new", UUID.randomUUID(),
            mock(UserDto.class), List.of()));

    MessageDto result = messageService.update(messageId, request);

    assertThat(result.content()).isEqualTo("new");
  }

  @Test
  @DisplayName("메시지 삭제 성공")
  void delete_Success() {
    UUID messageId = UUID.randomUUID();
    Message message = new Message("content", null, null, List.of());

    given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

    messageService.delete(messageId);

    verify(messageRepository).delete(message);
  }

  @Test
  @DisplayName("존재하지 않는 메시지 삭제 시 실패")
  void delete_Fail_NotFound() {
    UUID messageId = UUID.randomUUID();
    given(messageRepository.findById(messageId)).willReturn(Optional.empty());

    assertThrows(MessageNotFoundException.class, () -> messageService.delete(messageId));
  }
}