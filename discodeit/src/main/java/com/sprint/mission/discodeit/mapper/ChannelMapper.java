package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;


@Mapper(
    componentModel = "spring",
    uses = {UserMapper.class},
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ChannelMapper {

  @Mapping(target = "participants", source = "participants")
  @Mapping(target = "name", source = "name", defaultValue = "이름 없는 채널")
  @Mapping(target = "description", source = "description", defaultValue = "")
  ChannelDto toDto(Channel channel);

  default List<ChannelDto> toDtoList(List<Channel> channels) {
    if (channels == null) {
      return java.util.Collections.emptyList();
    }
    return channels.stream().map(this::toDto).toList();
  }
}