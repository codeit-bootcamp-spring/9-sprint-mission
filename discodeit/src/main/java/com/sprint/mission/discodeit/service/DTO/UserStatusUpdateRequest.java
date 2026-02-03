package com.sprint.mission.discodeit.service.DTO;

import java.time.Instant;
import java.util.UUID;

public record UserStatusUpdateRequest(
        UUID userStatusId,
        Instant lastViewAt
) {
}
