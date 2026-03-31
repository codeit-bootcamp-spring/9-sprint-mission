package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ForbiddenException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
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
  private UserRepository userRepository;
  @Mock
  private ChannelMapper channelMapper;
  @Mock
  private ReadStatusRepository readStatusRepository;

  @InjectMocks
  private BasicChannelService channelService;

  @Test
  @DisplayName("공개 채널 생성 성공")
  void createPublicChannel_Success() {
    UUID userId = UUID.randomUUID();
    User owner = new User("owner", "owner@test.com", "password", null);
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("General", "Desc");

    given(userRepository.findById(userId)).willReturn(Optional.of(owner));
    given(channelRepository.save(any(Channel.class))).willAnswer(inv -> inv.getArgument(0));
    given(channelMapper.toDto(any(Channel.class))).willReturn(
        new ChannelDto(UUID.randomUUID(), "PUBLIC", "General", "Desc", List.of(), null));

    ChannelDto result = channelService.createPublicChannel(request, userId);

    assertThat(result.name()).isEqualTo("General");
    verify(readStatusRepository).save(any());
  }

  @Test
  @DisplayName("비공개 채널 생성 성공")
  void createPrivateChannel_Success() {
    UUID creatorId = UUID.randomUUID();
    UUID participantId = UUID.randomUUID();
    User creator = new User("creator", "c@t.com", "pw", null);
    User participant = new User("participant", "p@t.com", "pw", null);
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of(participantId));

    given(userRepository.findById(creatorId)).willReturn(Optional.of(creator));

    given(userRepository.findAllById(anyIterable())).willReturn(List.of(participant));
    given(channelRepository.saveAndFlush(any(Channel.class))).willAnswer(inv -> inv.getArgument(0));
    given(channelMapper.toDto(any(Channel.class))).willReturn(
        new ChannelDto(UUID.randomUUID(), "PRIVATE", "비밀 대화방", "설명", List.of(), null));

    ChannelDto result = channelService.createPrivateChannel(request, creatorId);

    assertThat(result.type()).isEqualTo("PRIVATE");
    verify(channelRepository).saveAndFlush(any());
  }

  @Test
  @DisplayName("채널 목록 조회 성공")
  void findAllByUserId_Success() {
    UUID userId = UUID.randomUUID();
    List<Channel> channels = List.of(new Channel("Public", "Desc", ChannelType.PUBLIC, null));
    given(channelRepository.findAllByUserIdOrPublic(userId)).willReturn(channels);
    given(channelMapper.toDtoList(anyList())).willReturn(List.of());

    List<ChannelDto> result = channelService.findAllByUserId(userId);

    assertThat(result).isNotNull();
    verify(channelRepository).findAllByUserIdOrPublic(userId);
  }

  @Test
  @DisplayName("채널 수정 성공")
  void update_Success() {
    UUID channelId = UUID.randomUUID();
    UUID requesterId = UUID.randomUUID();
    User owner = new User("owner", "o@t.com", "pw", null);
    reflectSetId(owner, requesterId);

    Channel channel = new Channel("Old", "Old", ChannelType.PUBLIC, owner);
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("New", "New");

    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(channelMapper.toDto(any(Channel.class))).willReturn(
        new ChannelDto(channelId, "PUBLIC", "New", "New", List.of(), null));

    ChannelDto result = channelService.update(channelId, request, requesterId);

    assertThat(result.name()).isEqualTo("New");
  }

  @Test
  @DisplayName("비공개 채널 수정 시도 시 실패")
  void update_Fail_PrivateChannel() {
    UUID channelId = UUID.randomUUID();
    Channel channel = new Channel("Private", "Desc", ChannelType.PRIVATE, null);
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("New", "New");

    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

    assertThrows(PrivateChannelUpdateException.class,
        () -> channelService.update(channelId, request, UUID.randomUUID()));
  }

  @Test
  @DisplayName("권한 없는 유저의 채널 삭제 실패")
  void delete_Fail_Forbidden() {
    UUID channelId = UUID.randomUUID();
    User owner = new User("owner", "o@t.com", "pw", null);
    reflectSetId(owner, UUID.randomUUID());

    Channel channel = new Channel("Title", "Desc", ChannelType.PUBLIC, owner);
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

    assertThrows(ForbiddenException.class,
        () -> channelService.delete(channelId, UUID.randomUUID()));
  }

  @Test
  @DisplayName("존재하지 않는 채널 조회 실패")
  void findById_Fail_NotFound() {
    UUID channelId = UUID.randomUUID();
    given(channelRepository.findById(channelId)).willReturn(Optional.empty());

    assertThrows(ChannelNotFoundException.class, () -> channelService.findById(channelId));
  }

  private void reflectSetId(Object target, Object id) {
    try {
      java.lang.reflect.Field field;
      try {
        field = target.getClass().getDeclaredField("id");
      } catch (NoSuchFieldException e) {
        field = target.getClass().getSuperclass().getDeclaredField("id");
      }
      field.setAccessible(true);
      field.set(target, id);
    } catch (Exception e) {
    }
  }
}