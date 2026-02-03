package com.sprint.mission.discodeit.service.DTO;

import java.time.Instant;
import java.util.UUID;

public record UserStatusUpdateByUserIdRequest(
        UUID userId,
        Instant lastViewAt
) {
}
