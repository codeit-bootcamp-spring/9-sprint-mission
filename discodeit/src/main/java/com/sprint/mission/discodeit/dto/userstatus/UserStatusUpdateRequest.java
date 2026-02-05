package com.sprint.mission.discodeit.dto.userstatus;

import java.time.Instant;
import java.util.UUID;

public record UserStatusUpdateRequest(
        UUID userStatusId,
        Params params
) {
    public record Params(
            Instant lastActiveAt
    ) {
    }
}
