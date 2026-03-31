package com.sprint.mission.discodeit.service.basic;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.domain.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.domain.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.domain.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@ExtendWith(MockitoExtension.class)
class BasicMessageServiceTest {

  @InjectMocks
  private BasicMessageService messageService;

  @Mock
  private MessageRepository messageRepository;

  @Mock
  private ChannelRepository channelRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private MessageMapper messageMapper;

  @Mock
  private BinaryContentStorage binaryContentStorage;

  @Mock
  private BinaryContentRepository binaryContentRepository;

  @Mock
  private PageResponseMapper pageResponseMapper;

  @Test
  @DisplayName("메시지 생성 성공")
  void create_success() {
    // given
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest("안녕하세요", channelId, authorId);

    Channel channel = new Channel(ChannelType.PUBLIC, "채널명", "설명");
    User author = new User("홍길동", "test@test.com", "password123", null);
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(userRepository.findById(authorId)).willReturn(Optional.of(author));

    Message message = new Message("안녕하세요", channel, author, List.of());
    given(messageRepository.save(any())).willReturn(message);

    UserDto authorDto = new UserDto(authorId, "홍길동", "test@test.com", null, false);
    MessageDto messageDto = new MessageDto(
        message.getId(),
        Instant.now(),
        null,
        "안녕하세요",
        channelId,
        authorDto,
        List.of()
    );
    given(messageMapper.toDto(any(Message.class))).willReturn(messageDto);

    // when
    MessageDto result = messageService.create(request, List.of());

    // then
    assertThat(result).isNotNull();
    assertThat(result.content()).isEqualTo("안녕하세요");
    assertThat(result.author().username()).isEqualTo("홍길동");
  }

  @Test
  @DisplayName("메시지 생성 실패 - 채널 없음")
  void create_fail_channelNotFound() {
    // given
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest("안녕하세요", channelId, authorId);

    given(channelRepository.findById(channelId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> messageService.create(request, List.of()))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  @Test
  @DisplayName("메시지 생성 실패 - 유저 없음")
  void create_fail_authorNotFound() {
    // given
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest("안녕하세요", channelId, authorId);

    Channel channel = new Channel(ChannelType.PUBLIC, "채널명", "설명");
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(userRepository.findById(authorId)).willReturn(Optional.empty()); // 유저 없음

    // when & then
    assertThatThrownBy(() -> messageService.create(request, List.of()))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("메시지 수정 성공")
  void update_success() {
    // given
    UUID messageId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest("수정된 내용");

    Channel channel = new Channel(ChannelType.PUBLIC, "채널명", "설명");
    User author = new User("홍길동", "test@test.com", "password123", null);
    Message message = new Message("안녕하세요", channel, author, List.of());
    given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

    UserDto authorDto = new UserDto(UUID.randomUUID(), "홍길동", "test@test.com", null, false);
    MessageDto messageDto = new MessageDto(
        messageId,
        Instant.now(),
        Instant.now(),
        "수정된 내용",
        channelId,
        authorDto,
        List.of()
    );
    given(messageMapper.toDto(any(Message.class))).willReturn(messageDto);

    // when
    MessageDto result = messageService.update(messageId, request);

    // then
    assertThat(result).isNotNull();
    assertThat(result.content()).isEqualTo("수정된 내용");
  }

  @Test
  @DisplayName("메시지 수정 실패 - 메시지 없음")
  void update_fail_messageNotFound() {
    // given
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest("수정된 내용");

    given(messageRepository.findById(messageId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> messageService.update(messageId, request))
        .isInstanceOf(MessageNotFoundException.class);
  }

  @Test
  @DisplayName("메시지 삭제 성공")
  void delete_success() {
    // given
    UUID messageId = UUID.randomUUID();

    given(messageRepository.existsById(messageId)).willReturn(true);

    // when
    messageService.delete(messageId);

    // then
    then(messageRepository).should().deleteById(messageId);
  }

  @Test
  @DisplayName("메시지 삭제 실패 - 메시지 없음")
  void delete_fail_messageNotFound() {
    // given
    UUID messageId = UUID.randomUUID();

    given(messageRepository.existsById(messageId)).willReturn(false);

    // when & then
    assertThatThrownBy(() -> messageService.delete(messageId))
        .isInstanceOf(MessageNotFoundException.class);
  }

  @Test
  @DisplayName("채널 메시지 목록 조회 성공")
  void findAllByChannelId_success() {
    // given
    UUID channelId = UUID.randomUUID();
    Instant cursor = Instant.now();
    Pageable pageable = PageRequest.of(0, 50, Sort.by(Direction.DESC, "createdAt"));

    UserDto authorDto = new UserDto(UUID.randomUUID(), "홍길동", "test@test.com", null, false);
    MessageDto messageDto = new MessageDto(
        UUID.randomUUID(),
        Instant.now(),
        null,
        "안녕하세요",
        channelId,
        authorDto,
        List.of()
    );

    Slice<Message> slice = new SliceImpl<>(List.of(
        new Message("안녕하세요",
            new Channel(ChannelType.PUBLIC, "채널명", "설명"),
            new User("홍길동", "test@test.com", "password123", null),
            List.of())
    ));
    given(messageRepository.findAllByChannelIdWithAuthor(eq(channelId), any(), eq(pageable)))
        .willReturn(slice);
    given(messageMapper.toDto(any(Message.class))).willReturn(messageDto);

    given(pageResponseMapper.fromSlice(any(), any())).willAnswer(invocation -> new PageResponse<>(
        List.of(messageDto),
        null,
        1,
        false,
        1L
    ));

    // when
    PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, cursor, pageable);

    // then
    assertThat(result).isNotNull();
    assertThat(result.content()).hasSize(1);
    assertThat(result.content().get(0).content()).isEqualTo("안녕하세요");
  }
}
