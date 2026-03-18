package com.sprint.mission.discodeit.dto.data;

import java.time.Instant;
import java.util.UUID;

public record UserStatusDto(
    UUID id,
    UUID userId,
    Instant lastActiveAt,
    Boolean isOnline,
    Instant createdAt,
    Instant updatedAt
) {

}
