package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface ReadStatusMapper {

  @Mapping(target = "userId", expression = "java(readStatus.getUser().getId())")
  @Mapping(target = "channelId", expression = "java(readStatus.getChannel().getId())")
  ReadStatusDto toDto(ReadStatus readStatus);
}
