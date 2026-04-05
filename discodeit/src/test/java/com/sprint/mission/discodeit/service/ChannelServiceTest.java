package com.sprint.mission.discodeit.service;

import static com.sprint.mission.discodeit.entity.ChannelType.PRIVATE;
import static com.sprint.mission.discodeit.entity.ChannelType.PUBLIC;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.InvalidNameException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ChannelServiceTest {

  @Mock
  private ChannelRepository channelRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ReadStatusRepository readStatusRepository;

  @Mock
  private MessageRepository messageRepository;

  @Mock
  private ChannelMapper channelMapper;

  @InjectMocks
  private BasicChannelService channelService;

  @Test
  void create_public_success() {
    PublicChannelCreateRequest request =
        new PublicChannelCreateRequest("public-channel", "공개 채널입니다.");

    Channel channel = new Channel(ChannelType.PUBLIC, "public-channel", "공개 채널입니다.");

    ChannelDto mockDto = new ChannelDto(
        UUID.randomUUID(),
        ChannelType.PUBLIC,
        "public-channel",
        "공개 채널입니다.",
        List.of(),
        null
    );

    given(channelRepository.save(any(Channel.class)))
        .willReturn(channel);

    given(channelMapper.toDto(any(Channel.class)))
        .willReturn(mockDto);

    ChannelDto result = channelService.create(request);

    assertThat(result).isNotNull();
    assertThat(result.name()).isEqualTo("public-channel");
    assertThat(result.description()).isEqualTo("공개 채널입니다.");

    verify(channelRepository).save(any(Channel.class));
    verify(channelMapper).toDto(any(Channel.class));
  }

  @Test
  void create_public_fail_invalid_name() {
    PublicChannelCreateRequest request =
        new PublicChannelCreateRequest("", "공개 채널입니다.");

    assertThatThrownBy(() -> channelService.create(request))
        .isInstanceOf(InvalidNameException.class)
        .hasMessageContaining("입력 형식이 맞지 않습니다.");
  }

  @Test
  void create_private_success() {
    UUID userId = UUID.randomUUID();

    PrivateChannelCreateRequest request =
        new PrivateChannelCreateRequest(List.of(userId));

    Channel channel = new Channel(PRIVATE, "private-channel", "비공개 채널입니다.");

    ChannelDto mockDto = new ChannelDto(
        UUID.randomUUID(),
        ChannelType.PRIVATE,
        "private-channel",
        "비공개 채널입니다.",
        List.of(),
        null
    );

    given(channelRepository.save(any(Channel.class)))
        .willReturn(channel);

    given(userRepository.findAllById(any()))
        .willReturn(List.of());

    given(readStatusRepository.saveAll(any()))
        .willReturn(List.of());

    given(channelMapper.toDto(any(Channel.class)))
        .willReturn(mockDto);

    ChannelDto result = channelService.create(request);

    assertThat(result).isNotNull();
    assertThat(result.name()).isEqualTo("private-channel");

    then(channelRepository).should().save(any(Channel.class));
    then(userRepository).should().findAllById(any());
    then(readStatusRepository).should().saveAll(any());
    then(channelMapper).should().toDto(any(Channel.class));
  }

  @Test
  void create_private_fail_participantIds_null() {
    PrivateChannelCreateRequest request =
        new PrivateChannelCreateRequest(null);

    assertThatThrownBy(() -> channelService.create(request))
        .isInstanceOf(InvalidNameException.class);
  }

  @Test
  void update_success() {
    UUID channelId = UUID.randomUUID();

    PublicChannelUpdateRequest request =
        new PublicChannelUpdateRequest("public-channel", "공개 채널입니다.");

    Channel channel = new Channel(PUBLIC, "old-name", "old-description");

    ChannelDto mockDto = new ChannelDto(
        channelId,
        ChannelType.PUBLIC,
        "public-channel",
        "공개 채널입니다.",
        List.of(),
        null
    );

    given(channelRepository.findById(channelId))
        .willReturn(Optional.of(channel));

    given(channelMapper.toDto(any(Channel.class)))
        .willReturn(mockDto);

    ChannelDto result = channelService.update(channelId, request);

    assertThat(result).isNotNull();
    assertThat(result.name()).isEqualTo("public-channel");

    then(channelRepository).should().findById(channelId);
    then(channelMapper).should().toDto(any(Channel.class));
  }

  @Test
  void update_fail_channel_not_found() {
    UUID channelId = UUID.randomUUID();

    PublicChannelUpdateRequest request =
        new PublicChannelUpdateRequest("new-name-channel", "공개 채널입니다.");

    given(channelRepository.findById(channelId))
        .willReturn(Optional.empty());

    assertThatThrownBy(() -> channelService.update(channelId, request))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  @Test
  void delete_success() {
    UUID channelId = UUID.randomUUID();

    Channel channel = new Channel(PUBLIC, "public-channel", "공개 채널입니다.");

    given(channelRepository.existsById(channelId))
        .willReturn(true);

    channelService.delete(channelId);

    then(channelRepository).should().existsById(channelId);
    then(messageRepository).should().deleteAllByChannelId(channelId);
    then(readStatusRepository).should().deleteAllByChannelId(channelId);
    then(channelRepository).should().deleteById(channelId);
  }

  @Test
  void delete_fail_channel_not_found() {
    UUID channelId = UUID.randomUUID();

    given(channelRepository.existsById(channelId))
        .willReturn(false);

    assertThatThrownBy(() -> channelService.delete(channelId))
        .isInstanceOf(ChannelNotFoundException.class);

    then(messageRepository).shouldHaveNoInteractions();
    then(readStatusRepository).shouldHaveNoInteractions();
    then(channelRepository).should(never()).deleteById(any());
  }

  @Test
  void findByUserId_success() {
    UUID channelId = UUID.randomUUID();

    Channel channel = new Channel(
        PUBLIC,
        "public-channel",
        "공개 채널입니다."
    );

    ReflectionTestUtils.setField(channel, "id", channelId);
  }

  @Test
  void findByUserId_empty() {
    UUID userId = UUID.randomUUID();

    given(readStatusRepository.findAllByUserId(userId))
        .willReturn(List.of());

    given(channelRepository.findAllByTypeOrIdIn(
        ChannelType.PUBLIC,
        List.of()
    )).willReturn(List.of());

    List<ChannelDto> result = channelService.findAllByUserId(userId);

    assertThat(result).isEmpty();
  }
}