package com.sprint.mission.discodeit.service;

import static com.sprint.mission.discodeit.entity.ChannelType.PRIVATE;
import static com.sprint.mission.discodeit.entity.ChannelType.PUBLIC;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.error.ChannelNotFoundException;
import com.sprint.mission.discodeit.error.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.util.TestPropertyValues.Type;

@ExtendWith(MockitoExtension.class)
class ChannelServiceTest {

  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private ChannelMapper channelMapper;

  @Mock
  private UserRepository userRepository;

  @Mock
  private MessageRepository messageRepository;
  @Mock
  private ReadStatusRepository readStatusRepository;

  @InjectMocks
  private BasicChannelService channelService;

  @Test
  @DisplayName("PUBLIC 채널 생성 성공")
  void publicChannelCreateSuccessTest() {
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("public", "yeah");
    Channel savedChannel = new Channel(PUBLIC, "public", "yeah");

    ChannelDto expectedDto = new ChannelDto(UUID.randomUUID(), PUBLIC, "public", "yeah", null,
        null);
    given(channelRepository.save(any(Channel.class))).willReturn(savedChannel);

    given(channelMapper.toDto(any(Channel.class))).willReturn(expectedDto);
    ChannelDto result = channelService.create(request);

    assertThat(result.name()).isEqualTo(result.name());
    assertThat(result.description()).isEqualTo(result.description());
    verify(channelRepository).save(any(Channel.class));

  }

  @Test
  @DisplayName("PRIVATE 채널 생성 성공")
  void privateChannelCreateSuccessTset() {
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of());
    Channel savedChannel = new Channel(PRIVATE, "private", "wow");

    ChannelDto expectedDto = new ChannelDto(UUID.randomUUID(), PRIVATE, "private", "wow", null,
        null);

    given(channelRepository.save(any(Channel.class))).willReturn(savedChannel);
    given(channelMapper.toDto(any(Channel.class))).willReturn(expectedDto);

    ChannelDto result = channelService.create(request);

    assertThat(result.name()).isEqualTo(result.name());
    assertThat(result.description()).isEqualTo(result.description());
    verify(channelRepository).save(any(Channel.class));
  }


  @Test
  @DisplayName("PRIVATE 채널 생성 실패 - DB 조회 중 예외 발생")
  void privateChannelCreateFailTest() {
    List<UUID> userIds = List.of(UUID.randomUUID(), UUID.randomUUID());
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(userIds);

    given(userRepository.findAllById(anyList()))
        .willThrow(new UserNotFoundException(Map.of("error", "database error")));

    assertThatThrownBy(() -> channelService.create(request))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("채널 수정 성공")
  void channelUpdateSuccessTest() {
    UUID channelId = UUID.randomUUID();
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("새 이름", "새 설명");
    Channel channel = new Channel(PUBLIC, "옛날 이름", "옛날 설명");

    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(channelMapper.toDto(any())).willReturn(
        new ChannelDto(channelId, PUBLIC, "새 이름", "새 설명", null, null));

    ChannelDto result = channelService.update(channelId, request);

    assertThat(result.name()).isEqualTo("새 이름");
    verify(channelRepository).findById(channelId);
  }

  @Test
  @DisplayName("채널 수정 실패 - 존재하지 않는 ID")
  void channelUpdateFailTest() {
    UUID fakeChannelId = UUID.randomUUID();
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("수정 이름", "수정 설명");

    given(channelRepository.findById(fakeChannelId)).willReturn(Optional.empty());
    assertThatThrownBy(() -> channelService.update(fakeChannelId, request))
        .isInstanceOf(ChannelNotFoundException.class);

    verify(channelMapper, never()).toDto(any());
  }

  @Test
  @DisplayName("채널 삭제 성공")
  void channelDeleteSuccessTest() {
    UUID targetId = UUID.randomUUID();
    given(channelRepository.existsById(targetId)).willReturn(true);
    channelService.delete(targetId);
    verify(channelRepository).deleteById(targetId);
  }


  @Test
  @DisplayName("채널 삭제 실패 - 존재하지 않는 ID")
  void channelDeleteFailTest() {
    UUID channelId = UUID.randomUUID();
    given(channelRepository.existsById(channelId)).willReturn(false);
    assertThatThrownBy(() -> channelService.delete(channelId))
        .isInstanceOf(ChannelNotFoundException.class);
  }

}