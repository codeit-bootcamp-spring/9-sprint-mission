package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;

public interface UserStatusDto {
    record UpdateRequest(
            Instant lastAccessedAt
    ) {}

    record Response(
            UUID id,
            UUID userId,
            Instant lastAccessedAt
    ) {}
}