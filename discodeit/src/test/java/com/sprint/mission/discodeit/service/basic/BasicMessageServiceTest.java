package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
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
  private MessageMapper messageMapper;
  @Mock
  private PageResponseMapper pageResponseMapper;

  @InjectMocks
  private BasicMessageService basicMessageService;

  @DisplayName("create - 성공")
  @Test
  void create_Success() {
    // given
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest("Test Message", channelId, authorId);
    
    Channel channel = new Channel(ChannelType.PUBLIC, "Public", "Desc");
    User author = new User("user", "user@test.com", "pass", null);
    
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(userRepository.findById(authorId)).willReturn(Optional.of(author));
    
    Message savedMessage = new Message("Test Message", channel, author, List.of());
    MessageDto expectedDto = new MessageDto(savedMessage.getId(), null, null, "Test Message", null, null, null);
    
    given(messageMapper.toDto(any(Message.class))).willReturn(expectedDto);

    // when
    MessageDto result = basicMessageService.create(request, List.of());

    // then
    assertThat(result.content()).isEqualTo("Test Message");
    then(messageRepository).should().save(any(Message.class));
  }

  @DisplayName("create - 실패 (존재하지 않는 채널)")
  @Test
  void create_Fail_ChannelNotFound() {
    // given
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest("Test Message", channelId, authorId);
    
    given(channelRepository.findById(channelId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> basicMessageService.create(request, List.of()))
        .isInstanceOf(ChannelNotFoundException.class);
    
    then(messageRepository).should(never()).save(any());
  }

  @DisplayName("update - 성공")
  @Test
  void update_Success() {
    // given
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest("Updated Content");
    
    Channel channel = new Channel(ChannelType.PUBLIC, "Public", "Desc");
    User author = new User("user", "user@test.com", "pass", null);
    Message existingMessage = new Message("Old Content", channel, author, List.of());
    
    given(messageRepository.findById(messageId)).willReturn(Optional.of(existingMessage));
    
    MessageDto expectedDto = new MessageDto(messageId, null, null, "Updated Content", null, null, null);
    given(messageMapper.toDto(existingMessage)).willReturn(expectedDto);

    // when
    MessageDto result = basicMessageService.update(messageId, request);

    // then
    assertThat(result.content()).isEqualTo("Updated Content");
    assertThat(existingMessage.getContent()).isEqualTo("Updated Content");
  }

  @DisplayName("update - 실패 (존재하지 않는 메시지)")
  @Test
  void update_Fail_MessageNotFound() {
    // given
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest("Updated Content");
    
    given(messageRepository.findById(messageId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> basicMessageService.update(messageId, request))
        .isInstanceOf(MessageNotFoundException.class);
  }

  @DisplayName("delete - 성공")
  @Test
  void delete_Success() {
    // given
    UUID messageId = UUID.randomUUID();
    given(messageRepository.existsById(messageId)).willReturn(true);

    // when
    basicMessageService.delete(messageId);

    // then
    then(messageRepository).should().deleteById(messageId);
  }

  @DisplayName("delete - 실패 (존재하지 않는 메시지)")
  @Test
  void delete_Fail_MessageNotFound() {
    // given
    UUID messageId = UUID.randomUUID();
    given(messageRepository.existsById(messageId)).willReturn(false);

    // when & then
    assertThatThrownBy(() -> basicMessageService.delete(messageId))
        .isInstanceOf(MessageNotFoundException.class);
        
    then(messageRepository).should(never()).deleteById(any());
  }

  @DisplayName("findAllByChannelId - 성공")
  @Test
  @SuppressWarnings("unchecked")
  void findAllByChannelId_Success() {
    // given
    UUID channelId = UUID.randomUUID();
    Pageable pageable = PageRequest.of(0, 10);
    Instant now = Instant.now();
    
    Channel channel = new Channel(ChannelType.PUBLIC, "Public", "Desc");
    User author = new User("user", "user@test.com", "pass", null);
    Message message = new Message("Content", channel, author, List.of());
    MessageDto messageDto = new MessageDto(message.getId(), now, now, "Content", null, null, null);
    
    SliceImpl<Message> messageSlice = new SliceImpl<>(List.of(message), pageable, false);
    
    given(messageRepository.findAllByChannelIdWithAuthor(eq(channelId), any(Instant.class), eq(pageable)))
        .willReturn(messageSlice);
    given(messageMapper.toDto(message)).willReturn(messageDto);
    
    PageResponse<MessageDto> expectedResponse = new PageResponse<>(List.of(messageDto), null, 1, false, null);
    given(pageResponseMapper.fromSlice(any(Slice.class), any())).willReturn(expectedResponse);

    // when
    PageResponse<MessageDto> result = basicMessageService.findAllByChannelId(channelId, now, pageable);

    // then
    assertThat(result.content()).hasSize(1);
    assertThat(result.content().get(0).content()).isEqualTo("Content");
  }
}
