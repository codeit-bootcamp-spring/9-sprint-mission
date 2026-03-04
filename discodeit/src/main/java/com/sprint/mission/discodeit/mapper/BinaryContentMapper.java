package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BinaryContentMapper {
    public BinaryContentDto toDto(BinaryContent binaryContent){
      return new BinaryContentDto(
          binaryContent.getId(),
          binaryContent.getFileName(),
          binaryContent.getSize(),
          binaryContent.getContentType(),
          null
      );
    }
}
