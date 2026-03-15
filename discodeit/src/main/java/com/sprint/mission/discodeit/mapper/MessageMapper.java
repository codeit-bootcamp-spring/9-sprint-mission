package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, BinaryContentMapper.class})
public interface MessageMapper {

  @Mapping(source = "channel.id", target = "channelId")
  MessageDto toDto(Message message);

  List<MessageDto> toDtoList(List<Message> messages);
}