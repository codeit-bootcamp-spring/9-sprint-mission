package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import java.util.Comparator;
import java.time.Instant;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public abstract class ChannelMapper {

  @Autowired
  protected UserMapper userMapper;

  @Mapping(target = "participantIds", expression = "java(mapParticipants(channel))")
  @Mapping(target = "lastMessageAt", expression = "java(mapLastMessageAt(channel))")
  public abstract ChannelDto toDto(Channel channel);

  protected List<UserDto> mapParticipants(Channel channel) {
    if (!channel.getType().equals(ChannelType.PRIVATE)) {
      return List.of();
    }
    return channel.getReadStatuses().stream()
        .map(readStatus -> userMapper.toDto(readStatus.getUser()))
        .toList();
  }

  protected Instant mapLastMessageAt(Channel channel) {
    return channel.getMessages().stream()
        .map(Message::getCreatedAt)
        .max(Comparator.naturalOrder())
        .orElse(Instant.MIN);
  }
}