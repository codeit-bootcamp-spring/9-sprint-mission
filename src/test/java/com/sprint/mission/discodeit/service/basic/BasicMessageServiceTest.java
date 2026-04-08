package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
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
import java.util.function.Function;
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
    private BinaryContentStorage binaryContentStorage;
    @Mock
    private MessageMapper messageMapper;
    @Mock
    private PageResponseMapper pageResponseMapper;

    @InjectMocks
    private BasicMessageService messageService;

    @Test
    @DisplayName("create - 성공: 메시지를 생성한다")
    void create_success() {
        // given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        MessageCreateRequest request = new MessageCreateRequest("Hello!", channelId, authorId);

        Channel channel = new Channel(ChannelType.PUBLIC, "general", null);
        User author = new User("testuser", "test@email.com", "password1234", null);
        Message message = new Message("Hello!", channel, author, List.of());
        UserDto authorDto = new UserDto(authorId, "testuser", "test@email.com", null, true);
        MessageDto expectedDto = new MessageDto(UUID.randomUUID(), Instant.now(), Instant.now(),
            "Hello!", channelId, authorDto, List.of());

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(authorId)).willReturn(Optional.of(author));
        given(messageRepository.save(any(Message.class))).willReturn(message);
        given(messageMapper.toDto(any(Message.class))).willReturn(expectedDto);

        // when
        MessageDto result = messageService.create(request, List.of());

        // then
        assertThat(result).isNotNull();
        assertThat(result.content()).isEqualTo("Hello!");
        then(messageRepository).should().save(any(Message.class));
    }

    @Test
    @DisplayName("create - 실패: 채널이 존재하지 않으면 ChannelNotFoundException 발생")
    void create_fail_channelNotFound() {
        // given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        MessageCreateRequest request = new MessageCreateRequest("Hello!", channelId, authorId);

        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> messageService.create(request, List.of()))
            .isInstanceOf(ChannelNotFoundException.class);
    }

    @Test
    @DisplayName("update - 성공: 메시지 내용을 수정한다")
    void update_success() {
        // given
        UUID messageId = UUID.randomUUID();
        Channel channel = new Channel(ChannelType.PUBLIC, "general", null);
        User author = new User("testuser", "test@email.com", "password1234", null);
        Message message = new Message("old content", channel, author, List.of());
        MessageUpdateRequest request = new MessageUpdateRequest("new content");
        UserDto authorDto = new UserDto(UUID.randomUUID(), "testuser", "test@email.com", null,
            true);
        MessageDto expectedDto = new MessageDto(messageId, Instant.now(), Instant.now(),
            "new content", UUID.randomUUID(), authorDto, List.of());

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
        given(messageMapper.toDto(any(Message.class))).willReturn(expectedDto);

        // when
        MessageDto result = messageService.update(messageId, request);

        // then
        assertThat(result.content()).isEqualTo("new content");
    }

    @Test
    @DisplayName("update - 실패: 메시지가 존재하지 않으면 MessageNotFoundException 발생")
    void update_fail_notFound() {
        // given
        UUID messageId = UUID.randomUUID();
        MessageUpdateRequest request = new MessageUpdateRequest("new content");
        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> messageService.update(messageId, request))
            .isInstanceOf(MessageNotFoundException.class);
    }

    @Test
    @DisplayName("delete - 성공: 메시지를 삭제한다")
    void delete_success() {
        // given
        UUID messageId = UUID.randomUUID();
        Channel channel = new Channel(ChannelType.PUBLIC, "general", null);
        User author = new User("testuser", "test@email.com", "password1234", null);
        Message message = new Message("Hello!", channel, author, List.of());

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

        // when
        messageService.delete(messageId);

        // then
        then(messageRepository).should().delete(message);
    }

    @Test
    @DisplayName("delete - 실패: 메시지가 존재하지 않으면 MessageNotFoundException 발생")
    void delete_fail_notFound() {
        // given
        UUID messageId = UUID.randomUUID();
        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> messageService.delete(messageId))
            .isInstanceOf(MessageNotFoundException.class);
    }

    @Test
    @DisplayName("findAllByChannelId - 성공: 커서 없이 조회한다")
    @SuppressWarnings("unchecked")
    void findAllByChannelId_success_noCursor() {
        // given
        UUID channelId = UUID.randomUUID();
        Channel channel = new Channel(ChannelType.PUBLIC, "general", null);
        User author = new User("testuser", "test@email.com", "password1234", null);
        Message message = new Message("Hello!", channel, author, List.of());
        Slice<Message> slice = new SliceImpl<>(List.of(message), PageRequest.of(0, 50), false);
        PageResponse<MessageDto> expectedResponse = new PageResponse<>(List.of(), null, 50, false,
            null);

        given(messageRepository.findAllByChannelId(eq(channelId), any())).willReturn(slice);
        given(messageMapper.toDto(any(Message.class))).willReturn(
            new MessageDto(UUID.randomUUID(), Instant.now(), Instant.now(), "Hello!", channelId,
                null, List.of()));
        given(pageResponseMapper.fromSlice(any(Slice.class), any(Function.class)))
            .willReturn(expectedResponse);

        // when
        PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, null, 50);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("findAllByChannelId - 성공: 커서 기반으로 이전 메시지를 조회한다")
    @SuppressWarnings("unchecked")
    void findAllByChannelId_success_withCursor() {
        // given
        UUID channelId = UUID.randomUUID();
        Instant cursor = Instant.now();
        Slice<Message> slice = new SliceImpl<>(List.of(), PageRequest.of(0, 50), false);
        PageResponse<MessageDto> expectedResponse = new PageResponse<>(List.of(), null, 50, false, null);

        given(messageRepository.findAllByChannelIdAndCreatedAtBefore(eq(channelId), eq(cursor), any()))
            .willReturn(slice);
        given(pageResponseMapper.fromSlice(any(Slice.class), any(Function.class)))
            .willReturn(expectedResponse);

        // when
        PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, cursor, 50);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("findAllByChannelId - size가 0 이하이면 기본값 50을 사용한다")
    @SuppressWarnings("unchecked")
    void findAllByChannelId_defaultSize() {
        // given
        UUID channelId = UUID.randomUUID();
        Slice<Message> slice = new SliceImpl<>(List.of(), PageRequest.of(0, 50), false);
        PageResponse<MessageDto> expectedResponse = new PageResponse<>(List.of(), null, 50, false, null);

        given(messageRepository.findAllByChannelId(eq(channelId), any())).willReturn(slice);
        given(pageResponseMapper.fromSlice(any(Slice.class), any(Function.class)))
            .willReturn(expectedResponse);

        // when
        PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, null, 0);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("create - 실패: 작성자가 존재하지 않으면 UserNotFoundException 발생")
    void create_fail_userNotFound() {
        // given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        MessageCreateRequest request = new MessageCreateRequest("Hello!", channelId, authorId);
        Channel channel = new Channel(ChannelType.PUBLIC, "general", null);

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(authorId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> messageService.create(request, List.of()))
            .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("create - 성공: 첨부파일과 함께 메시지를 생성한다")
    void create_success_withAttachments() {
        // given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        MessageCreateRequest request = new MessageCreateRequest("Hello!", channelId, authorId);
        Channel channel = new Channel(ChannelType.PUBLIC, "general", null);
        User author = new User("testuser", "test@email.com", "password1234", null);
        byte[] fileBytes = "file".getBytes();
        BinaryContentCreateRequest attachReq = new BinaryContentCreateRequest("doc.pdf", "application/pdf", fileBytes);
        BinaryContent bc = new BinaryContent("doc.pdf", (long) fileBytes.length, "application/pdf");
        Message message = new Message("Hello!", channel, author, List.of(bc));
        MessageDto expectedDto = new MessageDto(UUID.randomUUID(), Instant.now(), Instant.now(),
            "Hello!", channelId, null, List.of());

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(authorId)).willReturn(Optional.of(author));
        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(bc);
        given(messageRepository.save(any(Message.class))).willReturn(message);
        given(messageMapper.toDto(any(Message.class))).willReturn(expectedDto);

        // when
        MessageDto result = messageService.create(request, List.of(attachReq));

        // then
        assertThat(result).isNotNull();
        then(binaryContentStorage).should().put(any(), any(byte[].class));
    }

    @Test
    @DisplayName("delete - 성공: 첨부파일이 있는 메시지를 삭제하면 첨부파일도 삭제된다")
    void delete_success_withAttachments() throws Exception {
        // given
        UUID messageId = UUID.randomUUID();
        Channel channel = new Channel(ChannelType.PUBLIC, "general", null);
        User author = new User("testuser", "test@email.com", "password1234", null);
        BinaryContent bc = new BinaryContent("doc.pdf", 100L, "application/pdf");
        java.lang.reflect.Field idField = bc.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(bc, UUID.randomUUID());

        Message message = new Message("Hello!", channel, author, List.of(bc));

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

        // when
        messageService.delete(messageId);

        // then
        then(messageRepository).should().delete(message);
        then(binaryContentStorage).should().delete(any(UUID.class));
        then(binaryContentRepository).should().deleteById(any(UUID.class));
    }
}
