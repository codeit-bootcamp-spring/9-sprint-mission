package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
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
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BasicChannelServiceTest {

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
  @DisplayName("create(public) 성공: 공개 채널을 저장하고 DTO를 반환한다")
  void createPublic_success() {
	PublicChannelCreateRequest request = new PublicChannelCreateRequest("general", "for everyone");
	Channel saved = new Channel(ChannelType.PUBLIC, "general", "for everyone");
	ChannelResponse expected = new ChannelResponse(UUID.randomUUID(), ChannelType.PUBLIC, "general",
		"for everyone", List.of(), Instant.now());

	given(channelRepository.save(any(Channel.class))).willReturn(saved);
	given(channelMapper.toResponse(saved)).willReturn(expected);

	ChannelResponse actual = channelService.create(request);

	assertSame(expected, actual);
	then(channelRepository).should().save(any(Channel.class));
	then(channelMapper).should().toResponse(saved);
  }

  @Test
  @DisplayName("create(public) 실패: 저장 중 예외가 발생하면 전파한다")
  void createPublic_fail_repositoryError() {
	PublicChannelCreateRequest request = new PublicChannelCreateRequest("general", "for everyone");

	given(channelRepository.save(any(Channel.class))).willThrow(new RuntimeException("db error"));

	assertThrows(RuntimeException.class, () -> channelService.create(request));

	then(channelRepository).should().save(any(Channel.class));
	then(channelMapper).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("create(private) 성공: 모든 참여자가 존재하면 읽음 상태를 생성한다")
  void createPrivate_success() {
	UUID participantA = UUID.randomUUID();
	UUID participantB = UUID.randomUUID();
	PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
		List.of(participantA, participantB));

	Channel saved = new Channel(ChannelType.PRIVATE, null, null);
	ReflectionTestUtils.setField(saved, "createdAt", Instant.now());
	User userA = new User("a", "a@test.com", "password123", null);
	User userB = new User("b", "b@test.com", "password123", null);
	ChannelResponse expected = new ChannelResponse(UUID.randomUUID(), ChannelType.PRIVATE, null, null,
		List.of(
			new UserResponse(participantA, "a", "a@test.com", null, false),
			new UserResponse(participantB, "b", "b@test.com", null, false)
		), Instant.now());

	given(channelRepository.save(any(Channel.class))).willReturn(saved);
	given(userRepository.findAllById(request.participantIds())).willReturn(List.of(userA, userB));
	given(channelMapper.toResponse(saved)).willReturn(expected);

	ChannelResponse actual = channelService.create(request);

	assertSame(expected, actual);
	then(readStatusRepository).should(times(2)).save(any(ReadStatus.class));
	then(channelMapper).should().toResponse(saved);
  }

  @Test
  @DisplayName("create(private) 실패: 일부 참여자가 없으면 예외가 발생한다")
  void createPrivate_fail_missingParticipant() {
	UUID participantA = UUID.randomUUID();
	UUID participantB = UUID.randomUUID();
	PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
		List.of(participantA, participantB));

	given(channelRepository.save(any(Channel.class))).willReturn(new Channel(ChannelType.PRIVATE, null, null));
	given(userRepository.findAllById(request.participantIds())).willReturn(
		List.of(new User("a", "a@test.com", "password123", null)));

	assertThrows(UserNotFoundException.class, () -> channelService.create(request));

	then(readStatusRepository).shouldHaveNoInteractions();
	then(channelMapper).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("update 성공: 공개 채널 이름/설명을 수정한다")
  void update_success() {
	UUID channelId = UUID.randomUUID();
	Channel channel = new Channel(ChannelType.PUBLIC, "before", "old");
	PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("after", "new");
	ChannelResponse expected = new ChannelResponse(channelId, ChannelType.PUBLIC, "after", "new",
		List.of(), Instant.now());

	given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
	given(channelMapper.toResponse(channel)).willReturn(expected);

	ChannelResponse actual = channelService.update(channelId, request);

	assertSame(expected, actual);
	assertEquals("after", channel.getName());
	assertEquals("new", channel.getDescription());
	then(channelMapper).should().toResponse(channel);
  }

  @Test
  @DisplayName("update 실패: 비공개 채널은 수정할 수 없다")
  void update_fail_privateChannel() {
	UUID channelId = UUID.randomUUID();
	Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);
	PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("after", "new");

	given(channelRepository.findById(channelId)).willReturn(Optional.of(privateChannel));

	assertThrows(PrivateChannelUpdateException.class,
		() -> channelService.update(channelId, request));

	then(channelMapper).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("delete 성공: 메시지/읽음 상태를 정리하고 채널을 삭제한다")
  void delete_success() {
	UUID channelId = UUID.randomUUID();
	Channel channel = new Channel(ChannelType.PUBLIC, "general", "desc");
	ReflectionTestUtils.setField(channel, "id", channelId);

	given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

	channelService.delete(channelId);

	then(messageRepository).should().deleteAllByChannel_Id(channelId);
	then(readStatusRepository).should().deleteAllByChannel_Id(channelId);
	then(channelRepository).should().delete(channel);
  }

  @Test
  @DisplayName("delete 실패: 채널이 없으면 예외가 발생한다")
  void delete_fail_channelNotFound() {
	UUID channelId = UUID.randomUUID();

	given(channelRepository.findById(channelId)).willReturn(Optional.empty());

	assertThrows(ChannelNotFoundException.class, () -> channelService.delete(channelId));

	then(channelRepository).should().findById(channelId);
	then(channelRepository).shouldHaveNoMoreInteractions();
	then(messageRepository).shouldHaveNoInteractions();
	then(readStatusRepository).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("findAllByUserId 성공: 공개 채널과 구독한 비공개 채널만 조회된다")
  void findAllByUserId_success() {
	UUID userId = UUID.randomUUID();
	UUID publicChannelId = UUID.randomUUID();
	UUID subscribedPrivateId = UUID.randomUUID();
	UUID notSubscribedPrivateId = UUID.randomUUID();

	Channel publicChannel = new Channel(ChannelType.PUBLIC, "public", "all");
	Channel subscribedPrivate = new Channel(ChannelType.PRIVATE, null, null);
	Channel notSubscribedPrivate = new Channel(ChannelType.PRIVATE, null, null);

	ReflectionTestUtils.setField(publicChannel, "id", publicChannelId);
	ReflectionTestUtils.setField(subscribedPrivate, "id", subscribedPrivateId);
	ReflectionTestUtils.setField(notSubscribedPrivate, "id", notSubscribedPrivateId);

	User user = new User("jun", "jun@test.com", "password123", null);
	ReadStatus readStatus = new ReadStatus(user, subscribedPrivate, Instant.now());

	ChannelResponse publicDto = new ChannelResponse(publicChannelId, ChannelType.PUBLIC, "public", "all",
		List.of(), Instant.now());
	ChannelResponse privateDto = new ChannelResponse(subscribedPrivateId, ChannelType.PRIVATE, null, null,
		List.of(new UserResponse(userId, "jun", "jun@test.com", null, false)), Instant.now());

	given(readStatusRepository.findAllByUser_Id(userId)).willReturn(List.of(readStatus));
	given(channelRepository.findAll()).willReturn(
		List.of(publicChannel, subscribedPrivate, notSubscribedPrivate));
	given(channelMapper.toResponse(publicChannel)).willReturn(publicDto);
	given(channelMapper.toResponse(subscribedPrivate)).willReturn(privateDto);

	List<ChannelResponse> result = channelService.findAllByUserId(userId);

	assertEquals(2, result.size());
	assertEquals(List.of(publicDto, privateDto), result);
  }

  @Test
  @DisplayName("findAllByUserId 실패: 읽음 상태 조회 중 예외가 발생하면 전파한다")
  void findAllByUserId_fail_readStatusError() {
	UUID userId = UUID.randomUUID();

	given(readStatusRepository.findAllByUser_Id(userId)).willThrow(new RuntimeException("db error"));

	assertThrows(RuntimeException.class, () -> channelService.findAllByUserId(userId));

	then(readStatusRepository).should().findAllByUser_Id(userId);
	then(channelRepository).shouldHaveNoInteractions();
	then(channelMapper).shouldHaveNoInteractions();
  }
}
