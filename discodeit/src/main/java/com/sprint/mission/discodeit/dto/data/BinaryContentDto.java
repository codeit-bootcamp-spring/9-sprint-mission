package com.sprint.mission.discodeit.dto.data;

import java.time.Instant;
import java.util.UUID;

public record BinaryContentDto(
    UUID id,
    String fileName,
    Long size,
    String contentType,
    Instant createdAt,
    Instant updatedAt
) {

}