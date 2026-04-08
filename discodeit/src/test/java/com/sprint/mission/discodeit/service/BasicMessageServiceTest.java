package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

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
  private com.sprint.mission.discodeit.mapper.PageResponseMapper pageResponseMapper;
  @Mock
  private BinaryContentStorage binaryContentStorage;

  @InjectMocks
  private BasicMessageService messageService;

  @Test
  @DisplayName("메시지 생성 성공 테스트 - 첨부파일 없음")
  void create_message_success() {
    // Given
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest("안녕하세요!", channelId, authorId);

    given(channelRepository.findById(channelId)).willReturn(
        Optional.of(org.mockito.Mockito.mock(Channel.class)));
    given(userRepository.findById(authorId)).willReturn(
        Optional.of(org.mockito.Mockito.mock(User.class)));

    Message mockMessage = org.mockito.Mockito.mock(Message.class);
    given(messageRepository.save(any(Message.class))).willReturn(mockMessage);

    // When
    // 두 번째 인자로 '빈 리스트'를 던져줍니다
    messageService.create(request, Collections.emptyList());

    // Then
    verify(messageRepository).save(any(Message.class));
  }

  @Test
  @DisplayName("메시지 수정 성공 테스트")
  void update_message_success() {
    // Given
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest("수정된 내용");
    Message mockMessage = org.mockito.Mockito.mock(Message.class);

    given(messageRepository.findById(messageId)).willReturn(Optional.of(mockMessage));
    given(messageRepository.save(any(Message.class))).willReturn(mockMessage);

    // When
    messageService.update(messageId, request);

    // Then
    verify(messageRepository).save(any(Message.class));
  }

  @Test
  @DisplayName("메시지 삭제 성공 테스트")
  void delete_message_success() {
    // Given
    UUID messageId = UUID.randomUUID();
    Message mockMessage = org.mockito.Mockito.mock(Message.class);
    given(messageRepository.findById(messageId)).willReturn(Optional.of(mockMessage));

    // When
    messageService.delete(messageId);

    // Then
    verify(messageRepository).deleteById(messageId);
  }

  @Test
  @DisplayName("채널 ID로 메시지 목록 조회 테스트")
  void find_all_messages_by_channel_id() {
    // Given
    UUID channelId = UUID.randomUUID();

    // 진짜 데이터가 담긴 슬라이스를 만듭니다.
    org.springframework.data.domain.Slice<Message> mockSlice =
        new org.springframework.data.domain.SliceImpl<>(
            java.util.List.of(org.mockito.Mockito.mock(Message.class)));

    // 창고(Repository) 대답 설정
    given(messageRepository.findAllByChannel_Id(any(UUID.class),
        any(org.springframework.data.domain.Pageable.class)))
        .willReturn(mockSlice);

    com.sprint.mission.discodeit.dto.response.PageResponse mockPageResponse =
        org.mockito.Mockito.mock(com.sprint.mission.discodeit.dto.response.PageResponse.class);

    given(pageResponseMapper.fromSlice(any())).willReturn(mockPageResponse);

    // When
    var result = messageService.findAllByChannelId(channelId, 0);

    // Then
    assertThat(result).isNotNull();
    verify(messageRepository).findAllByChannel_Id(any(UUID.class),
        any(org.springframework.data.domain.Pageable.class));
  }

  @Test
  @DisplayName("메시지 수정 실패 테스트 - 존재하지 않는 메시지")
  void update_message_fail() {
    // Given
    UUID messageId = UUID.randomUUID();
    given(messageRepository.findById(messageId)).willReturn(java.util.Optional.empty());

    // When & Then
    org.assertj.core.api.Assertions.assertThatThrownBy(() ->
            messageService.update(messageId,
                new com.sprint.mission.discodeit.dto.MessageUpdateRequest("내용")))
        .isInstanceOf(com.sprint.mission.discodeit.exception.MessageException.class);
  }
}