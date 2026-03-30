package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.Channel.ChannelAlreadyExistsException;
import com.sprint.mission.discodeit.exception.Channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.Channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.User.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
  @Mock
  private MessageRepository messageRepository;

  @InjectMocks
  private BasicChannelService channelService;

  @Nested
  @DisplayName("채널 생성 테스트")
  class createChannel {

    @Test
    @DisplayName("중복되는 이름으로 인해 공용채널 생성 실패!")
    void fail_createChanel_with_validateName() {
      PublicChannelCreateRequest request = PublicChannelCreateRequest.builder()
          .name("공용1").description("공용임!").build();
      given(channelRepository.existsByName("공용1")).willReturn(true);
      assertThatThrownBy(() -> channelService.create(request)).isInstanceOf(
          ChannelAlreadyExistsException.class);
      verify(channelRepository, never()).save(any());

    }

    @Test
    @DisplayName("공용 채널 생성 완료")
    void success_createPublicChannel() {
      PublicChannelCreateRequest request = PublicChannelCreateRequest.builder()
          .name("공용채널1").description("yeah").build();
      given(channelRepository.existsByName("공용채널1")).willReturn(false);
      given(channelRepository.save(any(Channel.class))).willAnswer(inv -> inv.getArgument(0));
      ChannelDto dto = ChannelDto.builder().name(request.name()).description(request.description())
          .build();
      given(channelMapper.toDto(any(Channel.class))).willReturn(dto);
      ChannelDto result = channelService.create(request);

      assertThat(result).isNotNull();
      assertThat(result.name()).isEqualTo("공용채널1");

      verify(channelRepository).save(any(Channel.class));

    }


    @Test
    @DisplayName("존재하지 않는 참가자를 포함함으로써 비밀 채널 생성 실패")
    void fail_CreatePublicChannel_because_ParticipantsNotFound() {
      UUID user1 = UUID.randomUUID();
      UUID user2 = UUID.randomUUID();
      List<UUID> participantsIds = List.of(user1, user2);
      User user = mock(User.class);
      given(user.getId()).willReturn(user1);
      given(userRepository.findAllById(participantsIds)).willReturn(List.of(user));

      PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantsIds);

      assertThatThrownBy(() -> channelService.create(request)).isInstanceOf(
          DiscodeitException.class);

      verify(channelRepository, never()).save(any());
    }

    @Test
    @DisplayName("비밀 채널 생성 성공")
    void success_create_privateChannel() {
      UUID user1 = UUID.randomUUID();
      UUID user2 = UUID.randomUUID();
      List<UUID> participantsIds = List.of(user1, user2);
      User mockUser1 = mock(User.class);
      User mockUser2 = mock(User.class);
      List<User> users = List.of(mockUser1, mockUser2);
      given(userRepository.findAllById(participantsIds)).willReturn(users);
      given(channelRepository.save(any(Channel.class))).willAnswer(inv -> inv.getArgument(0));
      UserDto userDto1 = UserDto.builder().id(user1).username("user1").build();
      UserDto userDto2 = UserDto.builder().id(user2).username("user2").build();
      List<UserDto> userDtos = List.of(userDto1, userDto2);

      var request = new PrivateChannelCreateRequest(participantsIds);
      UUID channelId = UUID.randomUUID();
      var dto = ChannelDto.builder().id(channelId)
          .type(ChannelType.PRIVATE).participants(userDtos).build();
      given(channelMapper.toDto(any(Channel.class))).willReturn(dto);
      ChannelDto result = channelService.create(request);

      assertThat(result).isNotNull();
      assertThat(result.id()).isEqualTo(dto.id());
      assertThat(result.type()).isEqualTo(ChannelType.PRIVATE);

      verify(channelRepository).save(any());


    }


  }


  @Nested
  @DisplayName("채널 수정 테스트")
  class channelUpdate {

    @Test
    @DisplayName("존재하지 않는 채널 ID 수정 시도로 수정 실패")
    void fail_updateChannel_from_notExistsChannelId() {
      UUID notExistId = UUID.randomUUID();
      var request = PublicChannelUpdateRequest.builder().newName("갱갱").newDescription("뉸뉴").build();
      given(channelRepository.findById(notExistId)).willReturn(Optional.empty());
      assertThatThrownBy(() -> channelService.update(notExistId, request)).isInstanceOf(
          ChannelNotFoundException.class);

      verify(channelRepository, never()).save(any());
    }

    @Test
    @DisplayName("프라이빗 채널 수정 시도로 수정 실패")
    void fail_updateChannel_because_tryUpdatePrivateChannel() {
      UUID privateId = UUID.randomUUID();
      Channel channel = Channel.builder().type(ChannelType.PRIVATE).build();

      given(channelRepository.findById(privateId)).willReturn(Optional.of(channel));
      var request = PublicChannelUpdateRequest.builder().build();
      assertThatThrownBy(() -> channelService.update(privateId, request)).isInstanceOf(
          PrivateChannelUpdateException.class);

      verify(channelRepository, never()).save(any());
    }

    @Test
    @DisplayName("채널 수정 성공")
    void success_updatePublicChannel() {
      UUID channelId = UUID.randomUUID();

      var channel = Channel.builder().name("공용채널").type(ChannelType.PUBLIC).description("공용채널입니다.")
          .build();

      given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

      var request = PublicChannelUpdateRequest.builder().newName("공용2").newDescription("공용2입니당")
          .build();
      given(channelRepository.existsByName("공용2")).willReturn(false);
      var dto = ChannelDto.builder().id(channelId).type(ChannelType.PUBLIC).name(request.newName())
          .description(request.newDescription()).build();
      given(channelMapper.toDto(any(Channel.class))).willReturn(dto);
      var result = channelService.update(channelId, request);

      assertThat(result).isNotNull();
      assertThat(result.id()).isEqualTo(channelId);
      assertThat(result.type()).isEqualTo(ChannelType.PUBLIC);


    }


  }

  @Nested
  @DisplayName("채널 삭제 테스트")
  class deleteChannelTest {

    @Test
    @DisplayName("존재하지 않는 채널 삭제 시도->채널 삭제 실패")
    void fail_deleteChannel_because_tryNotExistsChannelId() {
      UUID channelId = UUID.randomUUID();
//      given(channelRepository.existsById(channelId)).willReturn(false);
      assertThatThrownBy(() -> channelService.delete(channelId)).isInstanceOf(
          ChannelNotFoundException.class);

    }

    @Test
    @DisplayName("채널 삭제 테스트")
    void success_deleteChannel() {
      UUID id = UUID.randomUUID();
      given(channelRepository.existsById(id)).willReturn(true);

      channelService.delete(id);
      verify(messageRepository).deleteAllByChannelId(id);
      verify(readStatusRepository).deleteAllByChannelId(id);
      verify(channelRepository).deleteById(id);

    }

  }

  @Nested
  @DisplayName("유저 Id로 채널 조회")
  class findAllByUserId {

    @Test
    @DisplayName("존재하지 않는 유저 Id로 채널 조회 시도->실패")
    void delete_findAllByUserId_with_NotExistsUserId() {
      UUID userId = UUID.randomUUID();
      assertThatThrownBy(() -> channelService.findAllByUserId(userId)).isInstanceOf(
          UserNotFoundException.class);
    }

    @Test
    @DisplayName("유저 Id로 해당 Id를 가진 유저가 속한 채널 전체 조회 성공")
    void success_findAllByUserId() {
      UUID userId = UUID.randomUUID();
      given(userRepository.existsById(userId)).willReturn(true);
      Channel channel = Channel.builder().build();
      List<Channel> channels = List.of(channel);
      given(channelRepository.findAllAccessibleByUserId(userId)).willReturn(channels);
      ChannelDto dto = ChannelDto.builder().build();
      given(channelMapper.toDto(any(Channel.class))).willReturn(dto);
      List<ChannelDto> result = channelService.findAllByUserId(userId);

      assertThat(result).isNotNull();


    }
  }


}