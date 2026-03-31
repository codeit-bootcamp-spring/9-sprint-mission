package com.sprint.mission.discodeit.service.basic;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.domain.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.domain.ChannelUpdateNotAllowedException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
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

@ExtendWith(MockitoExtension.class)
class BasicChannelServiceTest {

  @InjectMocks
  private BasicChannelService channelService;

  @Mock
  private ChannelRepository channelRepository;

  @Mock
  private ReadStatusRepository readStatusRepository;

  @Mock
  private MessageRepository messageRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ChannelMapper channelMapper;

  @Test
  @DisplayName("PUBLIC 채널 생성 성공")
  void create_public_success() {
    // given
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("채널명", "채널 설명");

    Channel channel = new Channel(ChannelType.PUBLIC, "채널명", "채널 설명");
    given(channelRepository.save(any())).willReturn(channel);

    ChannelDto channelDto = new ChannelDto(channel.getId(), ChannelType.PUBLIC, "채널명", "채널 설명", null, null);
    given(channelMapper.toDto(any(Channel.class))).willReturn(channelDto);

    // when
    ChannelDto result = channelService.create(request);

    // then
    assertThat(result).isNotNull();
    assertThat(result.name()).isEqualTo("채널명");
    assertThat(result.type()).isEqualTo(ChannelType.PUBLIC);
  }

  @Test
  @DisplayName("PRIVATE 채널 생성 성공")
  void create_private_success() {
    // given
    List<UUID> participantIds = List.of(UUID.randomUUID(), UUID.randomUUID());
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);

    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    given(channelRepository.save(any())).willReturn(channel);
    given(userRepository.findAllById(participantIds)).willReturn(List.of(
        new User("유저1", "user1@test.com", "password123", null),
        new User("유저2", "user2@test.com", "password123", null)
    ));

    ChannelDto channelDto = new ChannelDto(channel.getId(), ChannelType.PRIVATE, null, null, List.of(), null);
    given(channelMapper.toDto(any(Channel.class))).willReturn(channelDto);

    // when
    ChannelDto result = channelService.create(request);

    // then
    assertThat(result).isNotNull();
    assertThat(result.type()).isEqualTo(ChannelType.PRIVATE);
  }

  @Test
  @DisplayName("채널 수정 성공")
  void update_success() {
    // given
    UUID channelId = UUID.randomUUID();
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("새채널명", "새채널설명");

    Channel channel = new Channel(ChannelType.PUBLIC, "채널명", "채널 설명");
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

    ChannelDto channelDto = new ChannelDto(channelId, ChannelType.PUBLIC, "새채널명", "새채널설명", null, null);
    given(channelMapper.toDto(any(Channel.class))).willReturn(channelDto);

    // when
    ChannelDto result = channelService.update(channelId, request);

    // then
    assertThat(result).isNotNull();
    assertThat(result.name()).isEqualTo("새채널명");
    assertThat(result.description()).isEqualTo("새채널설명");
  }

  @Test
  @DisplayName("채널 수정 실패 - 채널 없음")
  void update_fail_channelNotFound() {
    // given
    UUID channelId = UUID.randomUUID();
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("새채널명", "새채널설명");

    given(channelRepository.findById(channelId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> channelService.update(channelId, request))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  @Test
  @DisplayName("채널 수정 실패 - PRIVATE 채널 수정 시도")
  void update_fail_privateChannel() {
    // given
    UUID channelId = UUID.randomUUID();
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("새채널명", "새채널설명");

    Channel channel = new Channel(ChannelType.PRIVATE, null, null); // PRIVATE 채널
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

    // when & then
    assertThatThrownBy(() -> channelService.update(channelId, request))
        .isInstanceOf(ChannelUpdateNotAllowedException.class);
  }

  @Test
  @DisplayName("채널 삭제 성공")
  void delete_success() {
    // given
    UUID channelId = UUID.randomUUID();

    given(channelRepository.existsById(channelId)).willReturn(true);

    // when
    channelService.delete(channelId);

    // then
    then(messageRepository).should().deleteAllByChannelId(channelId);
    then(readStatusRepository).should().deleteAllByChannelId(channelId);
    then(channelRepository).should().deleteById(channelId);
  }

  @Test
  @DisplayName("유저의 채널 목록 조회 성공")
  void findAllByUserId_success() {
    // given
    UUID userId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();

    ReadStatus readStatus = new ReadStatus(
        new User("홍길동", "test@test.com", "password123", null),
        new Channel(ChannelType.PRIVATE, null, null),
        Instant.now()
    );
    given(readStatusRepository.findAllByUserId(userId)).willReturn(List.of(readStatus));

    Channel publicChannel = new Channel(ChannelType.PUBLIC, "공개채널", "설명");
    given(channelRepository.findAllByTypeOrIdIn(eq(ChannelType.PUBLIC), any()))
        .willReturn(List.of(publicChannel));

    ChannelDto channelDto = new ChannelDto(channelId, ChannelType.PUBLIC, "공개채널", "설명", List.of(), null);
    given(channelMapper.toDto(any(Channel.class))).willReturn(channelDto);

    // when
    List<ChannelDto> result = channelService.findAllByUserId(userId);

    // then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).type()).isEqualTo(ChannelType.PUBLIC);
  }

  @Test
  @DisplayName("유저의 채널 목록 조회 - 채널 없음")
  void findAllByUserId_empty() {
    // given
    UUID userId = UUID.randomUUID();

    given(readStatusRepository.findAllByUserId(userId)).willReturn(List.of());
    given(channelRepository.findAllByTypeOrIdIn(eq(ChannelType.PUBLIC), any()))
        .willReturn(List.of());

    // when
    List<ChannelDto> result = channelService.findAllByUserId(userId);

    // then
    assertThat(result).isEmpty();
  }
}
