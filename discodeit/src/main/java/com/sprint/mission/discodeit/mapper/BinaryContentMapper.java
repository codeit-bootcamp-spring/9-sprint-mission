package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BinaryContentMapper {

  @Mapping(
      target = "url",
      expression = "java(\"http://localhost:8080/api/binaryContents/\" + binaryContent.getId() + \"/download\")"
  )
  BinaryContentDto toDto(BinaryContent binaryContent);
}
