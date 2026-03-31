package com.sprint.mission.discodeit.service.basic;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.Collections;
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
  private ReadStatusRepository readStatusRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private ReadStatusMapper readStatusMapper;

  @InjectMocks
  private BasicReadStatusService readStatusService;

  @Test
  void findAllByUserId_Success() {
    UUID userId = UUID.randomUUID();
    User user = mock(User.class);
    Channel channel = mock(Channel.class);
    ReadStatus readStatus = mock(ReadStatus.class);

    given(userRepository.findById(any())).willReturn(Optional.of(user));

    given(channelRepository.findAllByUserIdOrPublic(any())).willReturn(
        Collections.singletonList(channel));

    given(channel.getId()).willReturn(UUID.randomUUID());
    given(readStatusRepository.findByUserIdAndChannelId(any(), any())).willReturn(
        Optional.of(readStatus));

    given(readStatusMapper.toDto(any())).willReturn(null);

    readStatusService.findAllByUserId(userId);

    verify(readStatusRepository, atLeastOnce()).findByUserIdAndChannelId(any(), any());
  }
}