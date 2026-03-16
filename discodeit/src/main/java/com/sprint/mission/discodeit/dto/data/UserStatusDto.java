package com.sprint.mission.discodeit.dto.data;

import java.time.Instant;
import java.util.UUID;

public record UserStatusDto(
        UUID userId,
        boolean online,
        Instant lastActiveAt
) {
}