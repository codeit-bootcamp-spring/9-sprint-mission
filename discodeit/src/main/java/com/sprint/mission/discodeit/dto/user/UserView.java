package com.sprint.mission.discodeit.dto.user;

import java.time.Instant;
import java.util.UUID;

public record UserView(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String username,
        String displayName,
        String email,
        String phoneNumber,
        UUID profileImageId,
        boolean online,
        Instant lastSeenAt
) {
}
