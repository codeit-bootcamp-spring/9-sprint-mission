package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageSliceMapper;
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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;

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
  private MessageMapper messageMapper;
  @Spy
  private PageSliceMapper pageSliceMapper = new PageSliceMapper();
  @Mock
  private BinaryContentStorage binaryContentStorage;

  @InjectMocks
  private BasicMessageService messageService;

  @Test
  @DisplayName("create 성공: 첨부파일과 함께 메시지를 생성한다")
  void create_success() {
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();

    MessageCreateRequest request = new MessageCreateRequest("hello", channelId, authorId);
    BinaryContentCreateRequest attachment =
        new BinaryContentCreateRequest("a.png", "image/png", new byte[]{1, 2, 3});

    Channel channel = new Channel(ChannelType.PUBLIC, "general", "desc");
    User author = new User("jun", "jun@test.com", "password123", null);

    BinaryContent storedAttachment = new BinaryContent("a.png", 3L, "image/png");
    UUID attachmentId = UUID.randomUUID();
    ReflectionTestUtils.setField(storedAttachment, "id", attachmentId);

    Message savedMessage = new Message("hello", channel, author);
    MessageResponse expected = new MessageResponse(UUID.randomUUID(), Instant.now(), null, "hello",
        channelId,
        new UserResponse(authorId, "jun", "jun@test.com", null, false), List.of());

    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(userRepository.findById(authorId)).willReturn(Optional.of(author));
    given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(storedAttachment);
    given(messageRepository.save(any(Message.class))).willReturn(savedMessage);
    given(messageMapper.toResponse(savedMessage)).willReturn(expected);

    MessageResponse actual = messageService.create(request, List.of(attachment));

    assertSame(expected, actual);
    then(binaryContentStorage).should().put(eq(attachmentId), eq(attachment.bytes()));
    then(messageRepository).should().save(any(Message.class));
    then(messageMapper).should().toResponse(savedMessage);
  }

  @Test
  @DisplayName("create 실패: 채널이 없으면 예외가 발생한다")
  void create_fail_channelNotFound() {
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest("hello", channelId, authorId);

    given(channelRepository.findById(channelId)).willReturn(Optional.empty());

    assertThrows(ChannelNotFoundException.class, () -> messageService.create(request, List.of()));

    then(channelRepository).should().findById(channelId);
    then(userRepository).shouldHaveNoInteractions();
    then(messageRepository).shouldHaveNoInteractions();
    then(messageMapper).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("update 성공: 메시지 내용을 수정하고 DTO를 반환한다")
  void update_success() {
    UUID messageId = UUID.randomUUID();
    Message message = new Message("before", new Channel(ChannelType.PUBLIC, "general", "desc"),
        new User("jun", "jun@test.com", "password123", null));
    MessageUpdateRequest request = new MessageUpdateRequest("after");
    MessageResponse expected = new MessageResponse(messageId, Instant.now(), Instant.now(), "after",
        UUID.randomUUID(), null, List.of());

    given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
    given(messageMapper.toResponse(message)).willReturn(expected);

    MessageResponse actual = messageService.update(messageId, request);

    assertSame(expected, actual);
    assertEquals("after", message.getContent());
    then(messageRepository).should().findById(messageId);
    then(messageMapper).should().toResponse(message);
  }

  @Test
  @DisplayName("update 실패: 메시지가 없으면 예외가 발생한다")
  void update_fail_messageNotFound() {
    UUID messageId = UUID.randomUUID();

    given(messageRepository.findById(messageId)).willReturn(Optional.empty());

    assertThrows(MessageNotFoundException.class,
        () -> messageService.update(messageId, new MessageUpdateRequest("after")));

    then(messageRepository).should().findById(messageId);
    then(messageMapper).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("delete 성공: 메시지가 존재하면 삭제한다")
  void delete_success() {
    UUID messageId = UUID.randomUUID();
    Message message = new Message("content", new Channel(ChannelType.PUBLIC, "general", "desc"),
        new User("jun", "jun@test.com", "password123", null));

    given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

    messageService.delete(messageId);

    then(messageRepository).should().findById(messageId);
    then(messageRepository).should().delete(message);
  }

  @Test
  @DisplayName("delete 실패: 메시지가 없으면 예외가 발생한다")
  void delete_fail_messageNotFound() {
    UUID messageId = UUID.randomUUID();

    given(messageRepository.findById(messageId)).willReturn(Optional.empty());

    assertThrows(MessageNotFoundException.class, () -> messageService.delete(messageId));

    then(messageRepository).should().findById(messageId);
    then(messageRepository).shouldHaveNoMoreInteractions();
  }

  @Test
  @DisplayName("findAllByChannelId 성공: 메시지 목록을 반환한다")
  void findAllByChannelId_success() {
    UUID channelId = UUID.randomUUID();
    Channel channel = new Channel(ChannelType.PUBLIC, "general", "desc");
    Instant cursor = Instant.parse("2026-03-31T00:00:00Z");
    PageRequest pageable = PageRequest.of(0, 20);

    Message m1 = new Message("first", channel, new User("a", "a@test.com", "password123", null));
    Message m2 = new Message("second", channel, new User("b", "b@test.com", "password123", null));
    Slice<Message> fetched = new SliceImpl<>(List.of(m1, m2), pageable, false);

    MessageResponse r1 = new MessageResponse(UUID.randomUUID(), Instant.now(), Instant.now(), "first",
        channelId, null, List.of());
    MessageResponse r2 = new MessageResponse(UUID.randomUUID(), Instant.now(), Instant.now(), "second",
        channelId, null, List.of());

    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(messageRepository.findByChannelIdWithCursor(eq(channelId), eq(cursor), eq(pageable)))
        .willReturn(fetched);
    given(messageMapper.toResponse(m1)).willReturn(r1);
    given(messageMapper.toResponse(m2)).willReturn(r2);

    PageResponse<MessageResponse> actual = messageService.findAllByChannelId(channelId, cursor, pageable);

    assertEquals(2, actual.content().size());
    assertSame(r1, actual.content().get(0));
    assertSame(r2, actual.content().get(1));
    assertEquals(20, actual.size());
    assertEquals(false, actual.hasNext());
    then(channelRepository).should().findById(channelId);
    then(messageRepository).should().findByChannelIdWithCursor(eq(channelId), eq(cursor), eq(pageable));
    then(messageMapper).should().toResponse(m1);
    then(messageMapper).should().toResponse(m2);
  }

  @Test
  @DisplayName("findAllByChannelId 실패: 채널이 없으면 예외가 발생한다")
  void findAllByChannelId_fail_channelNotFound() {
    UUID channelId = UUID.randomUUID();
    Instant cursor = Instant.parse("2026-03-31T00:00:00Z");

    given(channelRepository.findById(channelId)).willReturn(Optional.empty());

    assertThrows(ChannelNotFoundException.class,
        () -> messageService.findAllByChannelId(channelId, cursor, PageRequest.of(0, 20)));

    then(channelRepository).should().findById(channelId);
    then(messageRepository).shouldHaveNoInteractions();
  }
}
