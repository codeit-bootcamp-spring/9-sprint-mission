package com.sprint.mission.discodeit.dto.binarycontent;

import java.time.Instant;
import java.util.UUID;

public record BinaryContentView(
        UUID id,
        Instant createdAt,
        String contentType,
        String fileName,
        long size
) {
}