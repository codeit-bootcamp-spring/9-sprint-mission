package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChannelServiceTest {

  @Mock private ChannelRepository channelRepository;
  @Mock private ReadStatusRepository readStatusRepository;
  @Mock private MessageRepository messageRepository;
  @Mock private UserRepository userRepository;
  @Mock private ChannelMapper channelMapper;

  @InjectMocks
  private BasicChannelService channelService;

  @Test
  void create_public_success() {

    PublicChannelCreateRequest request =
        new PublicChannelCreateRequest("name", "desc");

    Channel channel = new Channel(ChannelType.PUBLIC, "name", "desc");

    ChannelDto dto = new ChannelDto(
        UUID.randomUUID(),
        ChannelType.PUBLIC,
        "name",
        "desc",
        List.of(),
        null
    );

    given(channelRepository.save(any(Channel.class))).willReturn(channel);
    given(channelMapper.toDto(any(Channel.class))).willReturn(dto);

    ChannelDto result = channelService.create(request);

    assertThat(result.name()).isEqualTo("name");
    then(channelRepository).should().save(any(Channel.class));
  }

  @Test
  void create_private_success() {

    UUID userId = UUID.randomUUID();

    PrivateChannelCreateRequest request =
        new PrivateChannelCreateRequest(List.of(userId));

    Channel channel = new Channel(ChannelType.PRIVATE, null, null);

    ChannelDto dto = new ChannelDto(
        UUID.randomUUID(),
        ChannelType.PRIVATE,
        null,
        null,
        List.of(),
        null
    );

    given(channelRepository.save(any(Channel.class))).willReturn(channel);
    given(userRepository.findAllById(request.participantIds()))
        .willReturn(List.of());
    given(channelMapper.toDto(any(Channel.class))).willReturn(dto);

    ChannelDto result = channelService.create(request);

    assertThat(result.type()).isEqualTo(ChannelType.PRIVATE);
    then(readStatusRepository).should().saveAll(any());
  }


  @Test
  void find_success() {

    UUID id = UUID.randomUUID();

    Channel channel = new Channel(ChannelType.PUBLIC, "name", "desc");

    ChannelDto dto = new ChannelDto(id, ChannelType.PUBLIC, "name", "desc", List.of(), null);

    given(channelRepository.findById(id)).willReturn(Optional.of(channel));
    given(channelMapper.toDto(channel)).willReturn(dto);

    ChannelDto result = channelService.find(id);

    assertThat(result.id()).isEqualTo(id);
  }

  @Test
  void find_fail_not_found() {

    UUID id = UUID.randomUUID();

    given(channelRepository.findById(id)).willReturn(Optional.empty());

    assertThatThrownBy(() -> channelService.find(id))
        .isInstanceOf(ChannelNotFoundException.class);
  }


  @Test
  void findAllByUserId_success() {

    UUID userId = UUID.randomUUID();

    given(readStatusRepository.findAllByUserId(userId))
        .willReturn(List.of());

    given(channelRepository.findAllByTypeOrIdIn(any(), any()))
        .willReturn(List.of());

    List<ChannelDto> result = channelService.findAllByUserId(userId);

    assertThat(result).isNotNull();
  }

  @Test
  void update_success() {

    UUID id = UUID.randomUUID();

    Channel channel = new Channel(ChannelType.PUBLIC, "old", "old");

    PublicChannelUpdateRequest request =
        new PublicChannelUpdateRequest("new", "new");

    ChannelDto dto = new ChannelDto(id, ChannelType.PUBLIC, "new", "new", List.of(), null);

    given(channelRepository.findById(id)).willReturn(Optional.of(channel));
    given(channelMapper.toDto(channel)).willReturn(dto);

    ChannelDto result = channelService.update(id, request);

    assertThat(result.name()).isEqualTo("new");
  }

  @Test
  void update_fail_private_channel() {

    UUID id = UUID.randomUUID();

    Channel channel = new Channel(ChannelType.PRIVATE, null, null);

    PublicChannelUpdateRequest request =
        new PublicChannelUpdateRequest("new", "new");

    given(channelRepository.findById(id)).willReturn(Optional.of(channel));

    assertThatThrownBy(() -> channelService.update(id, request))
        .isInstanceOf(PrivateChannelUpdateException.class);
  }

  @Test
  void update_fail_not_found() {

    UUID id = UUID.randomUUID();

    given(channelRepository.findById(id)).willReturn(Optional.empty());

    assertThatThrownBy(() -> channelService.update(id,
        new PublicChannelUpdateRequest("a", "b")))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  @Test
  void delete_success() {

    UUID id = UUID.randomUUID();

    given(channelRepository.existsById(id)).willReturn(true);

    channelService.delete(id);

    then(messageRepository).should().deleteAllByChannelId(id);
    then(readStatusRepository).should().deleteAllByChannelId(id);
    then(channelRepository).should().deleteById(id);
  }

  @Test
  void delete_fail_not_found() {

    UUID id = UUID.randomUUID();

    given(channelRepository.existsById(id)).willReturn(false);

    assertThatThrownBy(() -> channelService.delete(id))
        .isInstanceOf(ChannelNotFoundException.class);
  }
}