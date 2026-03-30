package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.ChannelPrivateUpdateForbiddenException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicChannelServiceTest {

  @Mock ChannelRepository channelRepository;
  @Mock ReadStatusRepository readStatusRepository;
  @Mock MessageRepository messageRepository;
  @Mock UserRepository userRepository;
  @Mock ChannelMapper channelMapper;

  @InjectMocks BasicChannelService channelService;

  @Test
  void createPublicChannel_success() {
    PublicChannelCreateRequest request =
        new PublicChannelCreateRequest("name", "desc");

    channelService.create(request);

    then(channelRepository).should().save(any(Channel.class));
  }

  @Test
  void createPrivateChannel_success() {
    List<UUID> ids = List.of(UUID.randomUUID(), UUID.randomUUID());

    given(userRepository.findAllById(ids))
        .willReturn(List.of(mock(User.class), mock(User.class)));

    channelService.create(new PrivateChannelCreateRequest(ids));

    then(readStatusRepository)
        .should()
        .saveAll(argThat((List<ReadStatus> list) -> list.size() == 2));
  }

  @Test
  void updateChannel_success() {
    UUID id = UUID.randomUUID();
    Channel channel = new Channel(ChannelType.PUBLIC, "name", "desc");

    given(channelRepository.findById(id)).willReturn(Optional.of(channel));
    given(channelMapper.toDto(any())).willReturn(mock(ChannelDto.class));

    assertDoesNotThrow(() ->
        channelService.update(id, mock(PublicChannelUpdateRequest.class)));
  }

  @Test
  void updateChannel_fail_channelNotFound() {
    UUID id = UUID.randomUUID();

    given(channelRepository.findById(id)).willReturn(Optional.empty());

    assertThrows(ChannelNotFoundException.class,
        () -> channelService.update(id, mock(PublicChannelUpdateRequest.class)));
  }

  @Test
  void updateChannel_fail_privateChannel() {
    UUID id = UUID.randomUUID();
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);

    given(channelRepository.findById(id)).willReturn(Optional.of(channel));

    assertThrows(ChannelPrivateUpdateForbiddenException.class,
        () -> channelService.update(id, mock(PublicChannelUpdateRequest.class)));
  }

  @Test
  void deleteChannel_success() {
    UUID id = UUID.randomUUID();

    given(channelRepository.existsById(id)).willReturn(true);

    channelService.delete(id);

    then(messageRepository).should().deleteAllByChannelId(id);
    then(readStatusRepository).should().deleteAllByChannelId(id);
    then(channelRepository).should().deleteById(id);
  }

  @Test
  void deleteChannel_fail_channelNotFound() {
    UUID id = UUID.randomUUID();

    given(channelRepository.existsById(id)).willReturn(false);

    assertThrows(ChannelNotFoundException.class,
        () -> channelService.delete(id));
  }

  @Test
  void findChannel_success() {
    UUID id = UUID.randomUUID();

    given(channelRepository.findById(id))
        .willReturn(Optional.of(mock(Channel.class)));
    given(channelMapper.toDto(any()))
        .willReturn(mock(ChannelDto.class));

    assertDoesNotThrow(() -> channelService.find(id));
  }

  @Test
  void findChannel_fail() {
    UUID id = UUID.randomUUID();

    given(channelRepository.findById(id)).willReturn(Optional.empty());

    assertThrows(ChannelNotFoundException.class,
        () -> channelService.find(id));
  }

  @Test
  void findAllByUserId_success() {
    UUID userId = UUID.randomUUID();

    given(readStatusRepository.findAllByUserId(userId))
        .willReturn(List.of());

    given(channelRepository.findAllByTypeOrIdIn(any(), any()))
        .willReturn(List.of());

    assertDoesNotThrow(() ->
        channelService.findAllByUserId(userId));
  }

  @Test
  void findAllByUserId_verifyMapping() {
    UUID userId = UUID.randomUUID();

    given(readStatusRepository.findAllByUserId(userId))
        .willReturn(List.of());

    given(channelRepository.findAllByTypeOrIdIn(any(), any()))
        .willReturn(List.of(mock(Channel.class)));

    given(channelMapper.toDto(any()))
        .willReturn(mock(ChannelDto.class));

    List<ChannelDto> result = channelService.findAllByUserId(userId);

    assertNotNull(result);
    then(channelMapper).should().toDto(any());
  }
}