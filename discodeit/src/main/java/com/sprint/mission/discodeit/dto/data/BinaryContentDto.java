package com.sprint.mission.discodeit.dto.data;

import java.time.LocalDateTime;
import java.util.UUID;

public record BinaryContentDto(
    UUID id,
    String fileName,
    String contentType,
    Long size,
    LocalDateTime createdAt
) {

  public BinaryContentDto {
    if (contentType == null || contentType.isBlank()) {
      contentType = "application/octet-stream";
    }
    if (fileName == null) {
      fileName = "unknown";
    }
    if (createdAt == null) {
      createdAt = LocalDateTime.now();
    }
  }

}