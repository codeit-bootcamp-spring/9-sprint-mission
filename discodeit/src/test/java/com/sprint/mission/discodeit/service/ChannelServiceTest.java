package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.MAP;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageAtProjection;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import java.time.Instant;
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
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class ChannelServiceTest {

  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private ReadStatusRepository readStatusRepository;
  @Mock
  private MessageRepository messageRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private UserMapper userMapper;
  @Mock
  private ChannelMapper channelMapper;

  @InjectMocks
  private BasicChannelService channelService;

  @Nested
  @DisplayName("채널 생성 테스트")
  class CreateTest {

    @Test
    @DisplayName("성공: 참여자가 정상적으로 존재할 경우, Private 채널 생성에 성공한다.")
    void createSuccessPrivateChannel() {
      // 1. Given: 테스트 환경 설정
      UUID id1 = UUID.randomUUID();
      UUID id2 = UUID.randomUUID();
      List<UUID> participantIds = List.of(id1, id2);
      PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);

      User user1 = new User("User1", "email1@test.com", "path1", null);
      User user2 = new User("User2", "email2@test.com", "path2", null);
      List<User> participants = List.of(user1, user2);

      // when
      given(userRepository.findAllById(participantIds)).willReturn(participants);
      given(channelRepository.save(any(Channel.class))).willAnswer(i -> i.getArguments()[0]);
      channelService.create(request);

      // 3. Then: 행위 검증
      then(readStatusRepository).should().saveAll(any());
      then(channelRepository).should().save(any(Channel.class));
    }

    @Test
    @DisplayName("실패: 참여자 수와 실제 사용자의 수가 맞지 않을 경우, Private 채널 생성에 실패한다.")
    void creteFailPrivateChannel() {
      // give
      UUID id1 = UUID.randomUUID();
      UUID id2 = UUID.randomUUID();
      List<UUID> participantIds = List.of(id1, id2);
      PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);

      User user1 = new User("User1", "email1@test.com", "path1", null);
      List<User> participants = List.of(user1);

      // when
      given(userRepository.findAllById(participantIds)).willReturn(participants);

      // then
      assertThatThrownBy(() -> channelService.create(request))
          .isInstanceOf(UserNotFoundException.class)
          .hasMessageContaining("존재하지 않는 사용자")
          .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND)
          .extracting("details", MAP)
          .containsEntry("요청한 사용자 수", request.participantIds().size())
          .containsEntry("실제 사용자 수", participants.size());
    }
  }

  @Nested
  @DisplayName("채널 수정 테스트")
  class UpdateTest {

    @Test
    @DisplayName("성공: Public 채널의 이름, 설명을 수정할 수 있다.")
    void updateSuccessPublicChannel() {
      // given
      UUID channelId = UUID.randomUUID();
      Channel channel = new Channel(ChannelType.PUBLIC, "이름", "설명");
      PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("새로운 이름", "새로운 설명");

      given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

      // when
      channelService.update(channelId, request);

      // then
      assertThat("새로운 이름").isEqualTo(channel.getName());
      assertThat("새로운 설명").isEqualTo(channel.getDescription());
      then(channelRepository).should().save(channel);
    }

    @Test
    @DisplayName("실패: Private 채널의 이름, 설명을 수정할 수 있다.")
    void updateFailPrivateChannel() {
      // given
      UUID channelId = UUID.randomUUID();
      Channel channel = new Channel(ChannelType.PRIVATE, "이름", "설명");
      PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("새로운 이름", "새로운 설명");

      given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

      // when & then

      assertThatThrownBy(() -> channelService.update(channelId, request))
          .isInstanceOf(PrivateChannelUpdateException.class)
          .hasMessageContaining("Private 채널은 수정할 수 없습니다")
          .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PRIVATE_CHANNEL_UPDATE)
          .extracting("details", MAP)
          .containsEntry("channelId", channelId);
    }
  }

  @Nested
  @DisplayName("채널 삭제 테스트")
  class DeleteTest {

    @Test
    @DisplayName("성공: 채널이 존재할 경우 삭제할 수 있다")
    void DeleteSuccess() {
      UUID channelId = UUID.randomUUID();
      Channel channel = new Channel(ChannelType.PUBLIC, "이름", "설명");

      given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
      channelService.delete(channelId);

      then(messageRepository).should().deleteAllByChannelId(any());
      then(readStatusRepository).should().deleteAllByChannel_Id(any());
      then(channelRepository).should().deleteById(any());
    }

    @Test
    @DisplayName("실패: 채널이 존재하지 않을 경우 ChannelNotFoundException 예외를 반환한다.")
    void DeleteFail() {
      UUID channelId = UUID.randomUUID();

      given(channelRepository.findById(channelId)).willReturn(Optional.empty());

      assertThatThrownBy(() -> channelService.delete(channelId))
          .isInstanceOf(ChannelNotFoundException.class)
          .hasMessageContaining("존재하지 않는 채널")
          .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CHANNEL_NOT_FOUND)
          .extracting("details", MAP)
          .containsEntry("channelId", channelId);
    }
  }

  @Nested
  @DisplayName("사용자별 채널 목록 조회 테스트")
  class FindByUserIdTest {

    @Test
    @DisplayName("성공: 사용자가 접근 가능한 공용/사설 채널 정보가 메시지 시간과 함께 조립되어 반환된다")
    void FindByUserIdSuccess() {
      // given
      UUID userId = UUID.randomUUID();
      UUID publicChannelId = UUID.randomUUID();
      UUID privateChannelId = UUID.randomUUID();

      // 1. Mock 객체 생성 (채널 2종류)
      Channel publicChannel = new Channel(ChannelType.PUBLIC, "공용방", "설명");
      ReflectionTestUtils.setField(publicChannel, "id", publicChannelId);

      Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);
      ReflectionTestUtils.setField(privateChannel, "id", privateChannelId);

      List<Channel> channels = List.of(publicChannel, privateChannel);
      List<UUID> channelIds = List.of(publicChannelId, privateChannelId);

      // 2. Mocking - Repository 행동 정의
      given(channelRepository.findAccessibleChannelsByUserId(userId)).willReturn(channels);

      // 마지막 메시지 시간 프로젝션 Mock
      Instant now = Instant.now();
      MessageAtProjection projection = mock(MessageAtProjection.class);
      given(projection.getChannelId()).willReturn(publicChannelId);
      given(projection.getLastAt()).willReturn(now);
      given(messageRepository.findLastMessageAtByChannelIds(channelIds)).willReturn(
          List.of(projection));

      // 읽기 상태(참가자) Mock
      User participantUser = new User("참가자", "p@email.com", "pass", null);
      UserDto participantDto = new UserDto(UUID.randomUUID(), "참가자", "e@ma.il", null, false);
      ReadStatus readStatus = mock(ReadStatus.class);
      given(readStatus.getChannel()).willReturn(privateChannel);
      given(readStatus.getUser()).willReturn(participantUser);

      given(readStatusRepository.findAllByChannelIdsWithUser(channelIds)).willReturn(
          List.of(readStatus));
      given(userMapper.toDto(participantUser)).willReturn(participantDto);

      // Mapper 결과 Mock (최종 반환 형태)
      given(channelMapper.toDto(any(), any(), any())).willReturn(mock(ChannelDto.class));

      // when
      List<ChannelDto> result = channelService.findByUserId(userId);

      // then
      assertThat(result).hasSize(2);

      // 로직 검증: Mapper가 올바른 인자로 호출되었는지 확인
      // 공용 채널: 참가자 List.of(), 메시지 시간 Now
      then(channelMapper).should().toDto(publicChannel, List.of(), now);

      // 사설 채널: 참가자 포함된 리스트, 메시지 시간 생성일(기본값)
      then(channelMapper).should().toDto(eq(privateChannel), argThat(list -> list.size() == 1),
          eq(privateChannel.getCreatedAt()));

      then(channelRepository).should().findAccessibleChannelsByUserId(userId);
    }

    @Test
    @DisplayName("성공: 접근 가능한 채널이 없으면 빈 리스트를 반환하고 추가 조회를 하지 않는다")
    void FindByUserIdEmpty() {
      // given
      UUID userId = UUID.randomUUID();
      given(channelRepository.findAccessibleChannelsByUserId(userId)).willReturn(List.of());

      // when
      List<ChannelDto> result = channelService.findByUserId(userId);

      // then
      assertThat(result).isEmpty();
      // 채널이 없으면 메시지나 참가자를 조회할 필요가 없음 (최적화 검증)
      then(messageRepository).shouldHaveNoInteractions();
      then(readStatusRepository).shouldHaveNoInteractions();
    }
  }
}
