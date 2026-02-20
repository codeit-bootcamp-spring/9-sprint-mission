package com.sprint.mission.discodeit.dto.userstatus;

import java.time.Instant;
import java.util.UUID;

public record UserStatusView(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        UUID userId,
        Instant lastSeenAt,
        boolean onlineNow
) {
}
