package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import java.time.Instant;
import java.util.UUID;

public record BinaryContentDto(
    UUID id,
    String fileName,
    Long size,
    String contentType,
    BinaryContentStatus status,
    Instant createdAt,
    Instant updatedAt
) {
  public BinaryContentDto(UUID id, String fileName, Long size, String contentType) {
    this(
        id,
        fileName,
        size,
        contentType,
        BinaryContentStatus.PROCESSING,
        Instant.now(),
        Instant.now()
    );
  }
}
