package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class ChannelMapper {

  public ChannelDto toDto(
      Channel channel,
      Instant latestMessageAt,
      List<UserDto> participants
  ) {
    return new ChannelDto(
        channel.getId(),
        channel.getName(),
        channel.getDescription(),
        channel.getChannelType(),
        latestMessageAt,
        participants,
        channel.getCreatedAt(),
        channel.getUpdatedAt()
    );
  }
}