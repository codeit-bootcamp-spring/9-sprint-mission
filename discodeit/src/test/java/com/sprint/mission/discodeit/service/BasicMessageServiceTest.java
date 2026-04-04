package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class BasicMessageServiceTest {

  @Mock private MessageRepository messageRepository;
  @Mock private ChannelRepository channelRepository;
  @Mock private UserRepository userRepository;
  @Mock private BinaryContentRepository binaryContentRepository;
  @Mock private BinaryContentStorage binaryContentStorage;
  @Mock private MessageMapper messageMapper;
  @Mock private PageResponseMapper pageResponseMapper;

  @InjectMocks private BasicMessageService basicMessageService;

  @Test
  @DisplayName("메시지 생성 성공 (첨부파일 없음)")
  void create_Success() {
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest("안녕하세요!", channelId, authorId);

    Channel mockChannel = new Channel(ChannelType.PUBLIC, "채널", "설명");
    User mockAuthor = new User("작성자", "email@test.com", "pw", null);

    given(channelRepository.findById(channelId)).willReturn(Optional.of(mockChannel));
    given(userRepository.findById(authorId)).willReturn(Optional.of(mockAuthor));

    basicMessageService.create(request, Collections.emptyList());

    then(messageRepository).should(times(1)).save(any(Message.class));
    then(binaryContentRepository).should(times(0)).save(any());
  }

  @Test
  @DisplayName("메시지 생성 실패 - 존재하지 않는 채널")
  void create_Fail_ChannelNotFound() {
    UUID invalidChannelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest("안녕하세요!", invalidChannelId, authorId);

    given(channelRepository.findById(invalidChannelId)).willReturn(Optional.empty());

    assertThrows(NoSuchElementException.class,
        () -> basicMessageService.create(request, Collections.emptyList()));

    then(messageRepository).should(times(0)).save(any(Message.class));
  }

  @Test
  @DisplayName("메시지 생성 실패 - 존재하지 않는 작성자")
  void create_Fail_AuthorNotFound() {
    UUID channelId = UUID.randomUUID();
    UUID invalidAuthorId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest("안녕하세요!", channelId, invalidAuthorId);
    Channel mockChannel = new Channel(ChannelType.PUBLIC, "채널", "설명");

    given(channelRepository.findById(channelId)).willReturn(Optional.of(mockChannel));
    given(userRepository.findById(invalidAuthorId)).willReturn(Optional.empty());

    assertThrows(NoSuchElementException.class,
        () -> basicMessageService.create(request, Collections.emptyList()));
  }

  @Test
  @DisplayName("메시지 수정 성공")
  void update_Success() {
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest("수정된 내용입니다.");

    Channel mockChannel = new Channel(ChannelType.PUBLIC, "채널", "설명");
    User mockUser = new User("username", "email@test.com", "pw", null);
    Message mockMessage = new Message("기존 내용", mockChannel, mockUser, Collections.emptyList());

    given(messageRepository.findById(messageId)).willReturn(Optional.of(mockMessage));

    basicMessageService.update(messageId, request);

    assertThat(mockMessage.getContent()).isEqualTo("수정된 내용입니다.");
    then(messageRepository).should(times(1)).save(mockMessage);
  }

  @Test
  @DisplayName("메시지 수정 실패 - 존재하지 않는 메시지")
  void update_Fail_MessageNotFound() {
    UUID invalidMessageId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest("수정된 내용입니다.");

    given(messageRepository.findById(invalidMessageId)).willReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () -> basicMessageService.update(invalidMessageId, request));
  }

  @Test
  @DisplayName("메시지 삭제 성공 (첨부파일 연관 삭제 포함)")
  void delete_Success() {
    UUID messageId = UUID.randomUUID();
    Channel mockChannel = new Channel(ChannelType.PUBLIC, "채널", "설명");
    User mockUser = new User("username", "email@test.com", "pw", null);
    Message mockMessage = new Message("기존 내용", mockChannel, mockUser, Collections.emptyList());

    given(messageRepository.findById(messageId)).willReturn(Optional.of(mockMessage));

    basicMessageService.delete(messageId);

    then(binaryContentRepository).should(times(1)).deleteAll(mockMessage.getAttachments());
    then(messageRepository).should(times(1)).deleteById(messageId);
  }

  @Test
  @DisplayName("메시지 삭제 실패 - 존재하지 않는 메시지")
  void delete_Fail_MessageNotFound() {
    UUID invalidMessageId = UUID.randomUUID();
    given(messageRepository.findById(invalidMessageId)).willReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () -> basicMessageService.delete(invalidMessageId));
    then(messageRepository).should(times(0)).deleteById(any());
  }

  @Test
  @DisplayName("메시지 목록 조회 성공 (커서가 명시적으로 주어진 경우)")
  void findAllByChannelId_Success_WithCursor() {
    UUID channelId = UUID.randomUUID();
    Instant explicitCursor = Instant.parse("2026-01-01T10:00:00Z");
    Pageable pageable = PageRequest.of(0, 20);

    given(messageRepository.findAllByChannelId(eq(channelId), eq(explicitCursor), eq(pageable)))
        .willReturn(Page.empty());

    basicMessageService.findAllByChannelId(channelId, explicitCursor, pageable);

    then(messageRepository).should(times(1)).findAllByChannelId(eq(channelId), eq(explicitCursor), eq(pageable));
    then(pageResponseMapper).should(times(1)).fromPage(any());
  }

  @Test
  @DisplayName("메시지 목록 조회 성공 (커서가 null일 때 현재 시간으로 대체)")
  void findAllByChannelId_Success_NullCursor() {
    UUID channelId = UUID.randomUUID();
    Pageable pageable = PageRequest.of(0, 20);

    given(messageRepository.findAllByChannelId(eq(channelId), any(Instant.class), eq(pageable)))
        .willReturn(Page.empty());

    basicMessageService.findAllByChannelId(channelId, null, pageable);

    then(messageRepository).should(times(1)).findAllByChannelId(eq(channelId), any(Instant.class), eq(pageable));
  }
}