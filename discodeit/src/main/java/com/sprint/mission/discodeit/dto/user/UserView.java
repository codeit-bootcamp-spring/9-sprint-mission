package com.sprint.mission.discodeit.dto.user;

import java.time.Instant;
import java.util.UUID;

/// 응답용 DTO
public record UserView(
        UUID id,
        String username,
        String displayName,
        String email,
        String phoneNumber,
        UUID profileImageId,
        boolean online,
        Instant lastSeenAt
) {
}
