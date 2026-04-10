package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.Channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.Message.MessageNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
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

@ExtendWith(MockitoExtension.class)
class BasicMessageServiceTest {

  @Mock
  private MessageRepository messageRepository;
  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private BinaryContentRepository binaryContentRepository;
  @Mock
  private MessageMapper mapper;
  @Mock
  private BinaryContentStorage storage;
  @InjectMocks
  private BasicMessageService messageService;

  @Nested
  @DisplayName("메시지 생성 테스트")
  class MessageCreate {

    @Test
    @DisplayName("존재하지 않는 채널 ID->메시지 생성 실패")
    void fail_createMessage_with_NotExistsChannelId() {
      UUID channelId = UUID.randomUUID();
      var request = MessageCreateRequest.builder().channelId(channelId).build();
      given(channelRepository.findById(channelId)).willReturn(Optional.empty());
      assertThatThrownBy(() -> messageService.create(request, null))
          .isInstanceOf(ChannelNotFoundException.class);
    }

    @Test
    @DisplayName("이미지 없는 메시지 생성 완료")
    void success_createMessageWithoutProfile() {
      Channel mockChannel = mock(Channel.class);
      User mockUser = mock(User.class);
      UUID channelId = UUID.randomUUID();
      UUID userId = UUID.randomUUID();
      given(mockChannel.getId()).willReturn(channelId);
      given(mockUser.getId()).willReturn(userId);
      given(channelRepository.findById(channelId)).willReturn(Optional.of(mockChannel));
      given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));

      var request = MessageCreateRequest.builder().content("안녕").channelId(channelId)
          .authorId(userId).build();
      messageService.create(request, null);
      verify(messageRepository).save(any());


    }


  }

  @Nested
  @DisplayName("채널아이디로 채널에 속한 메시지 조회")
  class findAllByChannelId {

    @Test
    @DisplayName("잘못된 채널 Id로 조회 -> 메시지 조회 실패")
    void fail_findAllByChannelId_with_wrongChannelId() {
      UUID channelId = UUID.randomUUID();
      given(channelRepository.existsById(channelId)).willReturn(false);
      assertThatThrownBy(
          () -> messageService.findAllByChannelId(channelId, null, null)).isInstanceOf(
          ChannelNotFoundException.class);

    }

    @Test
    @DisplayName("메시지 조회 성공")
    void success_findAllByChannelId() {
      UUID channelId = UUID.randomUUID();
      Instant cursor = Instant.now();
      Pageable pageable = PageRequest.of(0, 10);
      given(channelRepository.existsById(channelId)).willReturn(true);
      Message mockMessage = mock(Message.class);
      MessageDto mockDto = MessageDto.builder().id(UUID.randomUUID()).content("hi").build();
      Slice<Message> messageSlice = new SliceImpl<>(List.of(mockMessage), pageable, true);

      given(messageRepository.findByChannelIdAndCreatedAtBefore(eq(channelId), eq(cursor),
          any(Pageable.class)))
          .willReturn(messageSlice);
      given(mapper.toDto(mockMessage)).willReturn(mockDto);

      // 2. When: 실행
      PageResponse<MessageDto> response = messageService.findAllByChannelId(channelId, cursor,
          pageable);


    }

  }

  @Nested
  @DisplayName("메시지 수정 테스트")
  class updateMessageTest {

    @Test
    @DisplayName("존재하지 않는 메시지 Id->메시지 수정 실패")
    void fail_updateMessage_with_NotExistsMessageId() {
      UUID messageId = UUID.randomUUID();
      var request = new MessageUpdateRequest("nono");

      given(messageRepository.findById(messageId)).willReturn(Optional.empty());
      assertThatThrownBy(() -> messageService.update(messageId, request)).isInstanceOf(
          MessageNotFoundException.class);

    }

    @Test
    @DisplayName("메시지 수정 성공 테스트")
    void success_updateMessage() {
      UUID messageId = UUID.randomUUID();
      Message mockMessage = mock(Message.class);
//      given(mockMessage.getId()).willReturn(messageId);
      given(messageRepository.findById(messageId)).willReturn(Optional.of(mockMessage));

      MessageUpdateRequest request = new MessageUpdateRequest("궁궁");
      MessageDto dto = MessageDto.builder().content(request.newContent()).id(messageId).build();
      given(mapper.toDto(any(Message.class))).willReturn(dto);
      MessageDto result = messageService.update(messageId, request);

      assertThat(result).isNotNull();
      assertThat(result.id()).isEqualTo(messageId);


    }

  }

  @Nested
  @DisplayName("메시지 삭제 테스트")
  class deleteMessageTest {

    @Test
    @DisplayName("존재하지 않는 메시지 id로 삭제시도-> 메시지 삭제 실패")
    void fail_deleteMessage_with_NotExistsMessageId() {
      UUID messageId = UUID.randomUUID();
      given(messageRepository.findById(messageId)).willReturn(Optional.empty());
      assertThatThrownBy(() -> messageService.delete(messageId)).isInstanceOf(
          MessageNotFoundException.class);

    }

    @Test
    @DisplayName("메시지 삭제 성공 테스트")
    void success_deleteMessage() {
      UUID messageId = UUID.randomUUID();
      Message message = mock(Message.class);
      given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
      messageService.delete(messageId);


    }
  }
}