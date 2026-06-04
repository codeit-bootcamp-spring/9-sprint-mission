package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import java.util.UUID;

public record BinaryContentResponse(
    UUID id,
    String fileName,
    Long size,
    String contentType,
    BinaryContentStatus status
) {

  public BinaryContentResponse(UUID id, String fileName, Long size, String contentType) {
    this(id, fileName, size, contentType, BinaryContentStatus.PROCESSING);
  }
}
