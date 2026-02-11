package com.sprint.mission.discodeit.DTO.UserStatusService.Request;

import java.time.Instant;
import java.util.UUID;

public record UpdateUserStatusRequest(
        UUID id,
        UUID userId,
        Instant lastActiveAt
) {
}
