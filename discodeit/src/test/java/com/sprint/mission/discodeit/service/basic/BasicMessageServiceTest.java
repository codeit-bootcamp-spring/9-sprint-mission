package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class BasicMessageServiceTest {

  @InjectMocks
  private BasicMessageService messageService;

  @Mock private MessageRepository messageRepository;
  @Mock private ChannelRepository channelRepository;
  @Mock private UserRepository userRepository;
  @Mock private MessageMapper messageMapper;
  @Mock private BinaryContentStorage binaryContentStorage;
  @Mock private BinaryContentRepository binaryContentRepository;
  @Mock private PageResponseMapper pageResponseMapper;

  // ==========================================
  // 1. Create 메소드 테스트
  // ==========================================
  @Test
  @DisplayName("메시지 생성 성공 (첨부파일 없음)")
  void create_Success() {
    // Given
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest("안녕하세요!",channelId, authorId);

    Channel mockChannel = mock(Channel.class);
    User mockUser = mock(User.class);
    MessageDto mockDto = mock(MessageDto.class);

    given(channelRepository.findById(channelId)).willReturn(Optional.of(mockChannel));
    given(userRepository.findById(authorId)).willReturn(Optional.of(mockUser));
    given(messageMapper.toDto(any(Message.class))).willReturn(mockDto);

    // When
    MessageDto result = messageService.create(request, List.of()); // 첨부파일 빈 리스트

    // Then
    assertThat(result).isNotNull();
    then(messageRepository).should(times(1)).save(any(Message.class));
  }

  @Test
  @DisplayName("메시지 생성 실패 (존재하지 않는 채널)")
  void create_Fail_ChannelNotFound() {
    // Given
    UUID channelId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest("안녕하세요!",channelId, UUID.randomUUID() );

    given(channelRepository.findById(channelId)).willReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> messageService.create(request, List.of()))
        .isInstanceOf(ChannelNotFoundException.class);

    then(messageRepository).should(times(0)).save(any(Message.class));
  }

  // ==========================================
  // 2. Update 메소드 테스트
  // ==========================================
  @Test
  @DisplayName("메시지 수정 성공")
  void update_Success() {
    // Given
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest("수정된 내용");
    Message mockMessage = mock(Message.class);
    MessageDto mockDto = mock(MessageDto.class);

    given(messageRepository.findById(messageId)).willReturn(Optional.of(mockMessage));
    given(messageMapper.toDto(mockMessage)).willReturn(mockDto);

    // When
    MessageDto result = messageService.update(messageId, request);

    // Then
    assertThat(result).isNotNull();
    then(mockMessage).should(times(1)).update("수정된 내용");
  }

  @Test
  @DisplayName("메시지 수정 실패 (존재하지 않는 메시지)")
  void update_Fail_MessageNotFound() {
    // Given
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest("수정된 내용");

    given(messageRepository.findById(messageId)).willReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> messageService.update(messageId, request))
        .isInstanceOf(MessageNotFoundException.class);
  }

  // ==========================================
  // 3. Delete 메소드 테스트
  // ==========================================
  @Test
  @DisplayName("메시지 삭제 성공")
  void delete_Success() {
    // Given
    UUID messageId = UUID.randomUUID();
    given(messageRepository.existsById(messageId)).willReturn(true);

    // When
    messageService.delete(messageId);

    // Then
    then(messageRepository).should(times(1)).deleteById(messageId);
  }
}