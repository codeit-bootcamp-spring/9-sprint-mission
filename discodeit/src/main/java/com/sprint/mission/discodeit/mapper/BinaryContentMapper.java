package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Component;

@Component
public class BinaryContentMapper {

  public BinaryContentDto toDto(BinaryContent entity) {
    if (entity == null) {
      return null;
    }
    String imageUrl =
        "/api/binaryContents/" + entity.getId() + "/image?t=" + System.currentTimeMillis();

    return new BinaryContentDto(
        entity.getId(),
        entity.getFileName(),
        entity.getContentType(),
        entity.getSize(),
        imageUrl
    );
  }

}
