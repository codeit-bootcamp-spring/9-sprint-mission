package com.sprint.mission.discodeit.service;

import static jdk.dynalink.linker.support.Guards.isNull;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.error.ChannelNotFoundException;
import com.sprint.mission.discodeit.error.MessageNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.ArrayList;
import java.util.Collections;
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

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

  @Mock
  private MessageRepository messageRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private MessageMapper messageMapper;

  @Mock
  private BinaryContentStorage binaryContentStorage;
  @Mock
  private BinaryContentRepository binaryContentRepository;
  @Mock
  private PageResponseMapper pageResponseMapper;


  @InjectMocks
  private BasicMessageService messageService;

  @Test
  @DisplayName("메시지 생성 성공")
  void messageCreateSuccess() {
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest("내용", channelId, authorId);

    List<BinaryContentCreateRequest> attachments = new ArrayList<>();

    given(channelRepository.findById(channelId)).willReturn(Optional.of(org.mockito.Mockito.mock(
        Channel.class)));
    given(userRepository.findById(authorId)).willReturn(
        Optional.of(org.mockito.Mockito.mock(User.class)));

    messageService.create(request, attachments);

    verify(messageRepository).save(any(Message.class));
  }

  @Test
  @DisplayName("메시지 생성 실패 채널 없음")
  void messageCreateFail() {
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest("내용", channelId, authorId);
    List<BinaryContentCreateRequest> attachments = new ArrayList<>();

    given(channelRepository.findById(channelId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.create(request, attachments))
        .isInstanceOf(ChannelNotFoundException.class);

    verify(userRepository, never()).findById(any());
    verify(messageRepository, never()).save(any());
  }

  @Test
  @DisplayName("메시지 수정 성공")
  void messageUpdateSuccess() {
    UUID id = UUID.randomUUID();
    Message message = new Message("기존", null, null, new ArrayList<>());
    MessageDto dto = new MessageDto(id, null, null, "수정완료", null, null, null);

    given(messageRepository.findById(id)).willReturn(Optional.of(message));
    given(messageMapper.toDto(message)).willReturn(dto);

    MessageDto result = messageService.update(id, new MessageUpdateRequest("수정완료"));

    assertThat(result.content()).isEqualTo("수정완료");
    assertThat(message.getContent()).isEqualTo("수정완료");
  }

  @Test
  @DisplayName("메시지 수정 실패")
  void messageUpdateFail() {
    UUID id = UUID.randomUUID();
    given(messageRepository.findById(id)).willReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.update(id, new MessageUpdateRequest("내용")))
        .isInstanceOf(MessageNotFoundException.class);
  }

  @Test
  @DisplayName("메시지 삭제 성공")
  void messageDeleteSuccess() {
    UUID id = UUID.randomUUID();
    given(messageRepository.existsById(id)).willReturn(true);

    messageService.delete(id);

    then(messageRepository).should().deleteById(id);
  }

  @Test
  @DisplayName("메시지 삭제 실패 - 존재하지 않음")
  void messageDeleteFail() {
    UUID id = UUID.randomUUID();
    given(messageRepository.existsById(id)).willReturn(false);

    assertThatThrownBy(() -> messageService.delete(id))
        .isInstanceOf(MessageNotFoundException.class);
    then(messageRepository).should(never()).deleteById(any());
  }

  @Test
  @DisplayName("목록 조회 성공")
  void messagefindAllSuccess() {
    Message message = new Message("내용", null, null, new ArrayList<>());
    Slice<Message> slice = new SliceImpl<>(List.of(message));

    given(messageRepository.findAllByChannelIdWithAuthor(any(), any(), any()))
        .willReturn(slice);

    messageService.findAllByChannelId(UUID.randomUUID(), null, PageRequest.of(0, 10));

    then(pageResponseMapper).should().fromSlice(any(), any());
  }

  @Test
  @DisplayName("목록 조회 - 결과가 없는 경우")
  void messagefindAllFail() {
    Slice<Message> emptySlice = new SliceImpl<>(Collections.emptyList());

    given(messageRepository.findAllByChannelIdWithAuthor(any(), any(), any()))
        .willReturn(emptySlice);

    messageService.findAllByChannelId(UUID.randomUUID(), null, PageRequest.of(0, 10));

    then(pageResponseMapper).should().fromSlice(any(), isNull());
  }
}