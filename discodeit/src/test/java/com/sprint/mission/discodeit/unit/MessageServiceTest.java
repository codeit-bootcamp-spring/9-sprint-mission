package com.sprint.mission.discodeit.unit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.type.ChannelType;
import java.time.Instant;
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
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {
  @Mock
  private MessageRepository messageRepository;
  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private BinaryContentRepository binaryContentRepository;
  @Mock
  private BinaryContentStorage binaryContentStorage;
  @Mock
  private MessageMapper messageMapper;

  @InjectMocks
  private BasicMessageService messageService;

  @Test
  @DisplayName("메시지 생성 성공")
  void createMessageSuccess() {
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest("test", channelId, authorId);

    Channel channel = new Channel(ChannelType.PUBLIC, "test channel", "test");
    User author = new User("author", "password", "author@email.com", null);

    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(userRepository.findById(authorId)).willReturn(Optional.of(author));

    given(messageRepository.save(any(Message.class))).willAnswer(invocation -> invocation.getArgument(0));
    given(messageMapper.toDto(any(Message.class))).willReturn(mock(MessageDto.class));

    MessageDto result = messageService.create(request, Collections.emptyList());

    assertThat(result).isNotNull();
    then(messageRepository).should().save(any(Message.class));
  }

  @Test
  @DisplayName("메시지 생성 실패 ")
  void createMessageFail() {
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest("Hello", channelId, authorId);

    given(channelRepository.findById(channelId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.create(request, Collections.emptyList()))
        .isInstanceOf(ChannelNotFoundException.class);

    then(messageRepository).should(never()).save(any());
    then(binaryContentRepository).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("메시지 내용 수정 성공")
  void updateContentSuccess() {
    // given
    UUID messageId = UUID.randomUUID();

    Channel channel = new Channel(ChannelType.PUBLIC, "test channel", "test");
    User author = new User("author", "password", "author@email.com", null);

    String newContent = "new Content";
    Message message = new Message(channel, author, "test Content", Collections.emptyList());

    given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
    given(messageMapper.toDto(message)).willReturn(mock(MessageDto.class));

    messageService.updateContent(messageId, newContent);

    assertThat(message.getContent()).isEqualTo(newContent);
    then(messageRepository).should().findById(messageId);
  }

  @Test
  @DisplayName("메시지 수정 실패 - 메시지 존재하지 않음")
  void updateContentFail() {
    UUID messageId = UUID.randomUUID();
    given(messageRepository.findById(messageId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.updateContent(messageId, "new content"))
        .isInstanceOf(MessageNotFoundException.class);

    then(messageMapper).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("메시지 삭제 성공")
  void deleteMessageSuccess() {
    UUID messageId = UUID.randomUUID();
    Channel channel = new Channel(ChannelType.PUBLIC, "test channel", "test");
    User author = new User("author", "password", "author@email.com", null);

    BinaryContent attachment = new BinaryContent("file.txt", 100L, "text/plain");
    Message message = new Message(channel, author, "content", List.of(attachment));

    given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

    messageService.delete(messageId);

    then(binaryContentRepository).should().deleteAll(message.getAttachments());
    then(messageRepository).should().delete(message);
  }

  @Test
  @DisplayName("메시지 삭제 실패 - 메시지 존재하지 않음")
  void delete_Fail_MessageNotFound() {
    UUID messageId = UUID.randomUUID();
    given(messageRepository.findById(messageId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.delete(messageId))
        .isInstanceOf(MessageNotFoundException.class);

    then(messageRepository).should(never()).delete(any());
  }

  @Test
  @DisplayName("채널 ID로 메시지 목록 조회")
  void findAllByChannelIdSuccess() {
    UUID channelId = UUID.randomUUID();
    Instant cursor = Instant.now();
    Pageable pageable = PageRequest.of(0, 2);

    Message m1 = mock(Message.class);
    Message m2 = mock(Message.class);
    Message m3 = mock(Message.class);

    given(m2.getCreatedAt()).willReturn(Instant.now().minusSeconds(5));
    List<Message> mockMessages = List.of(m1, m2, m3);
    given(messageRepository.findAllByChannel_Id(any(), any(), any())).willReturn(mockMessages);
    given(messageRepository.countByChannel_Id(channelId)).willReturn(10L);

    PageResponse<MessageDto> response = messageService.findAllByChannelId(channelId, cursor, pageable);

    assertThat(response.content().size()).isEqualTo(2);
    assertThat(response.hasNext()).isTrue();
    assertThat(response.nextCursor()).isNotNull();
  }

  @Test
  @DisplayName("채널 메시지 조회 - 채널이 없는 경우")
  void findAllByChannelId_Empty() {
    UUID channelId = UUID.randomUUID();
    Instant cursor = Instant.now();
    Pageable pageable = PageRequest.of(0, 10);

    given(messageRepository.findAllByChannel_Id(eq(channelId), any(), any()))
        .willReturn(Collections.emptyList());
    given(messageRepository.countByChannel_Id(channelId)).willReturn(0L);

    PageResponse<MessageDto> response = messageService.findAllByChannelId(channelId, cursor, pageable);

    assertThat(response.content().size()).isEqualTo(0);
    assertThat(response.hasNext()).isFalse();
    assertThat(response.nextCursor()).isNull(); // 결과가 없으므로 다음 커서도 null
    assertThat(response.totalElements()).isEqualTo(0L);
  }

}
