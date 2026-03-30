package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicReadStatusServiceTest {

  @Mock
  ReadStatusRepository readStatusRepository;

  @Mock
  UserRepository userRepository;

  @Mock
  ChannelRepository channelRepository;

  @Mock
  ReadStatusMapper readStatusMapper;

  @InjectMocks
  BasicReadStatusService readStatusService;

  @Test
  void create_success_newReadStatus() {
    UUID userId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    User user = org.mockito.Mockito.mock(User.class);
    given(user.getId()).willReturn(userId);
    Channel channel = org.mockito.Mockito.mock(Channel.class);
    given(channel.getId()).willReturn(channelId);
    ReadStatus readStatus = new ReadStatus(user, channel, Instant.now());

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(readStatusRepository.findByUserIdAndChannelId(userId, channelId)).willReturn(Optional.empty());
    given(readStatusRepository.save(org.mockito.ArgumentMatchers.any(ReadStatus.class))).willReturn(readStatus);
    given(readStatusMapper.toDto(readStatus)).willReturn(new ReadStatusDto(UUID.randomUUID(), userId, channelId, Instant.now()));

    ReadStatusDto result = readStatusService.create(new ReadStatusCreateRequest(userId, channelId, Instant.now()));

    assertNotNull(result);
  }

  @Test
  void create_fail_userNotFound() {
    UUID userId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    assertThrows(NoSuchElementException.class,
        () -> readStatusService.create(new ReadStatusCreateRequest(userId, channelId, Instant.now())));
  }

  @Test
  void update_success() {
    UUID readStatusId = UUID.randomUUID();
    User user = new User("alice", "alice@test.com", "password123", null);
    Channel channel = new Channel(ChannelType.PUBLIC, "general", "desc");
    ReadStatus readStatus = new ReadStatus(user, channel, Instant.now());

    given(readStatusRepository.findById(readStatusId)).willReturn(Optional.of(readStatus));
    given(readStatusMapper.toDto(readStatus)).willReturn(new ReadStatusDto(readStatusId, UUID.randomUUID(), UUID.randomUUID(), Instant.now()));

    ReadStatusDto result = readStatusService.update(readStatusId, new ReadStatusUpdateRequest(Instant.now()));

    assertNotNull(result);
  }

  @Test
  void delete_fail_notFound() {
    UUID readStatusId = UUID.randomUUID();
    given(readStatusRepository.existsById(readStatusId)).willReturn(false);

    assertThrows(NoSuchElementException.class, () -> readStatusService.delete(readStatusId));
  }

  @Test
  void findAllByUserId_success() {
    UUID userId = UUID.randomUUID();
    User user = new User("alice", "alice@test.com", "password123", null);
    Channel channel = new Channel(ChannelType.PUBLIC, "general", "desc");
    ReadStatus readStatus = new ReadStatus(user, channel, Instant.now());

    given(readStatusRepository.findAllByUserId(userId)).willReturn(List.of(readStatus));
    given(readStatusMapper.toDto(readStatus)).willReturn(new ReadStatusDto(UUID.randomUUID(), userId, UUID.randomUUID(), Instant.now()));

    List<ReadStatusDto> result = readStatusService.findAllByUserId(userId);

    assertNotNull(result);
    then(readStatusRepository).should().findAllByUserId(userId);
  }
}


