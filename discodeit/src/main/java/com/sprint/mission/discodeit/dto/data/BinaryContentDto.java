package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;


public record BinaryContentDto (
   UUID id,
   String fileName,
   String contentType,
   Long size
 ) {

 public static BinaryContentDto from(BinaryContent entity) {
  return new BinaryContentDto(
      entity.getId(),
      entity.getFileName(),
      entity.getContentType(),
      entity.getSize()
  );
 }
}
