package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.MAP;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;
import static org.mockito.Mockito.mock;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

  @Mock
  private MessageRepository messageRepository;
  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private BinaryContentStorage binaryContentStorage;
  @Mock
  private BinaryContentRepository binaryContentRepository;
  @Mock
  private BinaryContentMapper binaryContentMapper;
  @Mock
  private MessageMapper messageMapper;
  @Mock
  private PageResponseMapper pageResponseMapper;

  @InjectMocks
  private BasicMessageService messageService;

  @Nested
  @DisplayName("메시지 생성 테스트")
  class CreateTest {

    @Test
    @DisplayName("성공: 사용자는 채널에 첨부파일을 포함한 메시지를 보낼 수 있다.")
    void CreateSuccessMessage() {
      // given
      UUID channelId = UUID.randomUUID();
      UUID authorId = UUID.randomUUID();
      Channel channel = new Channel(ChannelType.PUBLIC, "이름", null);
      User user = new User("이름", "e@mail.com", "pass", null);
      MessageCreateRequest request = new MessageCreateRequest("메시지", channelId, authorId);
      List<MultipartFile> attachments = new ArrayList<>();
      attachments.add(new MockMultipartFile(
          "이미지", "avatar.png", "image/png", new byte[]{}
      ));
      attachments.add(new MockMultipartFile(
          "이미지1", "avatar1.png", "image/png", new byte[]{}
      ));
      UserDto userDto = new UserDto(authorId, user.getUsername(), user.getEmail(), null, true);
      List<BinaryContentDto> binaryContentDto = List.of(
          new BinaryContentDto(UUID.randomUUID(), "avatar.png", 3324L, "image/png"),
          new BinaryContentDto(UUID.randomUUID(), "avatar1.png", 3324L, "image/png")
      );
      MessageDto messageDto = new MessageDto(UUID.randomUUID(), Instant.now(), Instant.now(), "메시지",
          channelId, userDto, binaryContentDto);

      // when
      given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
      given(userRepository.findById(authorId)).willReturn(Optional.of(user));
      given(binaryContentRepository.save(any(
          BinaryContent.class))).willAnswer(invocation -> invocation.getArgument(0));
      given(messageRepository.save(any(Message.class))).willAnswer(
          invocation -> invocation.getArgument(0));
      given(messageMapper.toDto(any(), any())).willReturn(messageDto);
      MessageDto result = messageService.create(request, attachments);

      // then
      assertNotNull(result);
      then(binaryContentRepository).should(times(2)).save(any());
      then(binaryContentStorage).should(times(2)).put(any(), any());
      then(messageRepository).should().save(any());
    }

    @Test
    @DisplayName("실패: 채널이 존재하지 않을 경우 ChannelNotFoundException을 발생시킨다")
    void CreateFailMessageForChannelNotFoundException() {
      UUID channelId = UUID.randomUUID();
      UUID authorId = UUID.randomUUID();
      MessageCreateRequest request = new MessageCreateRequest("메시지", channelId, authorId);

      given(channelRepository.findById(channelId)).willReturn(Optional.empty());

      assertThatThrownBy(() -> messageService.create(request, null))
          .isInstanceOf(ChannelNotFoundException.class)
          .hasMessageContaining("존재하지 않는 채널")
          .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CHANNEL_NOT_FOUND)
          .extracting("details", MAP)
          .containsEntry("channelId", channelId);
    }

    @Test
    @DisplayName("실패: 작성자가 존재하지 않을 경우 ChannelNotFoundException을 발생시킨다")
    void CreateFailMessageForUserNotFoundException() {
      UUID channelId = UUID.randomUUID();
      UUID authorId = UUID.randomUUID();
      Channel channel = new Channel(ChannelType.PUBLIC, "이름", "설명");
      MessageCreateRequest request = new MessageCreateRequest("메시지", channelId, authorId);

      given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

      assertThatThrownBy(() -> messageService.create(request, null))
          .isInstanceOf(UserNotFoundException.class)
          .hasMessageContaining("존재하지 않는 사용자")
          .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND)
          .extracting("details", MAP)
          .containsEntry("userId", authorId);
    }
  }

  @Nested
  @DisplayName("메시지 수정 테스트")
  class UpdateTest {

    @Test
    @DisplayName("성공: 메시지를 수정할 수 있다.")
    void UpdateSuccess() {
      // given
      UUID messageId = UUID.randomUUID();
      Channel channel = new Channel(ChannelType.PUBLIC, null, null);
      User user = new User("이름", "e@ma.il", "ps", null);
      Message message = new Message("메시지", channel, user, null);
      MessageUpdateRequest request = new MessageUpdateRequest("새 메시지");

      // when
      given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
      messageService.update(messageId, request);

      // then
      assertThat("새 메시지").isEqualTo(message.getContent());
    }

    @Test
    @DisplayName("실패: 존재하지 않는 메시지는 수정할 수 없다.")
    void UpdateFail() {
      // given
      UUID messageId = UUID.randomUUID();
      MessageUpdateRequest request = new MessageUpdateRequest("새 메시지");

      // when
      given(messageRepository.findById(messageId)).willReturn(Optional.empty());

      // then
      assertThatThrownBy(() -> messageService.update(messageId, request))
          .isInstanceOf(MessageNotFoundException.class)
          .hasMessageContaining("존재하지 않는 메시지")
          .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MESSAGE_NOT_FOUND)
          .extracting("details", MAP)
          .containsEntry("messageId", messageId);
    }
  }

  @Nested
  @DisplayName("메시지 삭제 테스트")
  class DeleteTest {

    @Test
    @DisplayName("성공: 메시지가 존재할 경우, 메시지를 삭제할 수 있다")
    void DeleteSuccess() {
      // given
      UUID messageId = UUID.randomUUID();
      Channel channel = new Channel(ChannelType.PUBLIC, null, null);
      User user = new User("이름", "e@ma.il", "ps", null);
      Message message = new Message("메시지", channel, user, null);

      // when
      given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
      messageService.delete(messageId);

      // then
      then(messageRepository).should().findById(messageId);
      then(messageRepository).should().delete(message);
    }

    @Test
    @DisplayName("실패: 메시지가 존재하지 않을 경우, MessageNotFoundException을 발생시킨다.")
    void DeleteFail() {
      // given
      UUID messageId = UUID.randomUUID();

      // when&then
      assertThatThrownBy(() -> messageService.delete(messageId))
          .isInstanceOf(MessageNotFoundException.class)
          .hasMessageContaining("존재하지 않는 메시지")
          .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MESSAGE_NOT_FOUND)
          .extracting("details", MAP)
          .containsEntry("messageId", messageId);
    }
  }

  @Nested
  @DisplayName("채널 내 메시지 조회 테스트")
  class FindAllInChannel {

    @Test
    @DisplayName("채널 ID와 커서로 메시지 목록을 조회할 때, 다음 커서가 올바르게 전달되는지 검증한다")
    void successFindAllByChannelId() {
      // Given
      UUID channelId = UUID.randomUUID();
      Instant cursor = Instant.now();
      Pageable pageable = PageRequest.of(0, 10);

      Message lastMessage = mock(Message.class);
      Instant expectedNextCursor = Instant.now().minusSeconds(100);
      PageResponse mockResponse = mock(PageResponse.class);
      List<Message> messageList = List.of(mock(Message.class), lastMessage);
      Slice<Message> messageSlice = new SliceImpl<>(messageList, pageable, true);

      given(lastMessage.getCreatedAt()).willReturn(expectedNextCursor);
      given(messageRepository.findOlderByChannelId(eq(channelId), eq(cursor), any(Pageable.class)))
          .willReturn(messageSlice);
      given(messageMapper.toDto(any(Message.class), any())).willReturn(mock(MessageDto.class));
      given(pageResponseMapper.fromSlice(any(Slice.class), eq(expectedNextCursor)))
          .willReturn(mockResponse);

      // When
      PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, cursor,
          pageable);

      // Then
      assertThat(result).isEqualTo(mockResponse);
      then(pageResponseMapper).should(times(1)).fromSlice(any(), eq(expectedNextCursor));
    }

    @Test
    @DisplayName("다음 페이지가 없는 경우 nextCursor는 null이 된다")
    void findAllByChannelId_ShouldReturnNullCursor_WhenHasNoNext() {
      // Given
      UUID channelId = UUID.randomUUID();
      Instant cursor = Instant.now();
      Pageable pageable = PageRequest.of(0, 2);

      List<Message> messages = List.of(mock(Message.class));
      Slice<Message> messageSlice = new SliceImpl<>(messages, pageable, false); // hasNext = false

      given(messageRepository.findOlderByChannelId(channelId, cursor, pageable))
          .willReturn(messageSlice);

      given(messageMapper.toDto(any(), any())).willReturn(mock(MessageDto.class));

      // When
      messageService.findAllByChannelId(channelId, cursor, pageable);

      // Then
      // nextCursor 자리에 null이 넘어갔는지 확인
      then(pageResponseMapper).should(times(1)).fromSlice(any(), eq(null));
    }
  }
}
