package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class, UserMapper.class})
public interface MessageMapper {

  @Mapping(target = "channelId", source = "channel.id")
  MessageDto toDto(Message message);

  default Instant map(LocalDateTime value) {
    if (value == null) {
      return null;
    }
    return value.atZone(ZoneId.systemDefault()).toInstant();
  }

  List<MessageDto> toDtoList(List<Message> messages);
}
