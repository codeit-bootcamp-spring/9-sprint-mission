package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
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
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicChannelServiceTest {

    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private ReadStatusRepository readStatusRepository;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BinaryContentRepository binaryContentRepository;
    @Mock
    private BinaryContentStorage binaryContentStorage;
    @Mock
    private ChannelMapper channelMapper;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private BasicChannelService channelService;

    @Test
    @DisplayName("create(public) - 성공: PUBLIC 채널을 생성한다")
    void createPublic_success() {
        // given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("general", "일반 채널");
        Channel channel = new Channel(ChannelType.PUBLIC, "general", "일반 채널");
        ChannelDto expectedDto = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "general",
            "일반 채널", List.of(), null);

        given(channelRepository.save(any(Channel.class))).willReturn(channel);
        given(messageRepository.findLastCreatedAtByChannelId(any())).willReturn(Optional.empty());
        given(channelMapper.toDto(any(Channel.class), any(), any())).willReturn(expectedDto);

        // when
        ChannelDto result = channelService.create(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.type()).isEqualTo(ChannelType.PUBLIC);
        assertThat(result.name()).isEqualTo("general");
    }

    @Test
    @DisplayName("create(private) - 성공: PRIVATE 채널을 생성하고 ReadStatus를 생성한다")
    void createPrivate_success() {
        // given
        UUID userId = UUID.randomUUID();
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of(userId));
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        User user = new User("testuser", "test@email.com", "password1234", null);
        UserDto userDto = new UserDto(userId, "testuser", "test@email.com", null, true);
        ChannelDto expectedDto = new ChannelDto(UUID.randomUUID(), ChannelType.PRIVATE, null, null,
            List.of(userDto), null);

        given(channelRepository.save(any(Channel.class))).willReturn(channel);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(readStatusRepository.save(any())).willReturn(null);
        given(userMapper.toDto(user)).willReturn(userDto);
        given(channelMapper.toDto(any(Channel.class), any(), any())).willReturn(expectedDto);

        // when
        ChannelDto result = channelService.create(request);

        // then
        assertThat(result.type()).isEqualTo(ChannelType.PRIVATE);
        then(readStatusRepository).should().save(any());
    }

    @Test
    @DisplayName("create(private) - 실패: 참가자가 존재하지 않으면 UserNotFoundException 발생")
    void createPrivate_fail_userNotFound() {
        // given
        UUID userId = UUID.randomUUID();
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of(userId));
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);

        given(channelRepository.save(any(Channel.class))).willReturn(channel);
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> channelService.create(request))
            .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("update - 성공: 채널명과 설명을 수정한다")
    void update_success() {
        // given
        UUID channelId = UUID.randomUUID();
        Channel channel = new Channel(ChannelType.PUBLIC, "old-name", "old-desc");
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("new-name", "new-desc");
        ChannelDto expectedDto = new ChannelDto(channelId, ChannelType.PUBLIC, "new-name",
            "new-desc", List.of(), null);

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(messageRepository.findLastCreatedAtByChannelId(channelId)).willReturn(
            Optional.empty());
        given(channelMapper.toDto(any(Channel.class), any(), any())).willReturn(expectedDto);

        // when
        ChannelDto result = channelService.update(channelId, request);

        // then
        assertThat(result.name()).isEqualTo("new-name");
    }

    @Test
    @DisplayName("update - 실패: PRIVATE 채널은 수정할 수 없다")
    void update_fail_privateChannel() {
        // given
        UUID channelId = UUID.randomUUID();
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

        // when & then
        assertThatThrownBy(() -> channelService.update(channelId,
            new PublicChannelUpdateRequest("name", "desc")))
            .isInstanceOf(PrivateChannelUpdateException.class);
    }

    @Test
    @DisplayName("delete - 성공: 채널을 삭제한다")
    void delete_success() {
        // given
        UUID channelId = UUID.randomUUID();
        Channel channel = new Channel(ChannelType.PUBLIC, "general", "일반 채널");

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(messageRepository.findAllByChannel_Id(channelId)).willReturn(List.of());

        // when
        channelService.delete(channelId);

        // then
        then(channelRepository).should().delete(channel);
    }

    @Test
    @DisplayName("delete - 실패: 채널이 존재하지 않으면 ChannelNotFoundException 발생")
    void delete_fail_notFound() {
        // given
        UUID channelId = UUID.randomUUID();
        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> channelService.delete(channelId))
            .isInstanceOf(ChannelNotFoundException.class);
    }

    @Test
    @DisplayName("findAllByUserId - 성공: 사용자의 채널 목록을 조회한다")
    void findAllByUserId_success() {
        // given
        UUID userId = UUID.randomUUID();
        Channel channel = new Channel(ChannelType.PUBLIC, "general", null);
        ChannelDto channelDto = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "general",
            null, List.of(), null);

        given(channelRepository.findAllByUserWithDetails(eq(ChannelType.PUBLIC), eq(userId)))
            .willReturn(List.of(channel));
        given(messageRepository.findLastCreatedAtByChannelIds(any())).willReturn(List.of());
        given(channelMapper.toDto(any(Channel.class), any(), any())).willReturn(channelDto);

        // when
        List<ChannelDto> result = channelService.findAllByUserId(userId);

        // then
        assertThat(result).hasSize(1);
    }
}
