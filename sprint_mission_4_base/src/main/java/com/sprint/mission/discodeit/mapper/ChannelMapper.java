package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ChannelMapper {

  @Mapping(target = "participants", source = "readStatuses")
  @Mapping(target = "lastMessageAt", expression = "java(calculateLastMessageAt(entity))")
  ChannelDto toDto(Channel entity);

  @Mapping(target = ".", source = "user")
  UserDto mapReadStatusToUserDto(ReadStatus readStatus);

  default Instant calculateLastMessageAt(Channel entity) {
    if (entity == null || entity.getMessages() == null) {
      return null;
    }
    return entity.getMessages().stream()
        .map(Message::getCreatedAt)
        .max(Comparator.naturalOrder())
        .orElse(entity.getCreatedAt());
  }
}