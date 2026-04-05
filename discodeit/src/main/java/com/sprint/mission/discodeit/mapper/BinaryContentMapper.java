package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BinaryContentMapper {

  BinaryContentResponse toResponse(BinaryContent binaryContent);
}
