package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import org.mapstruct.Mapper;

import java.time.Instant;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ChannelMapper {

  default ChannelDto toDto(
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