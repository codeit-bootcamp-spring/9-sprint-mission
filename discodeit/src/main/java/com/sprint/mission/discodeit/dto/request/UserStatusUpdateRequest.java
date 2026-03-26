package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.PastOrPresent;
import java.time.Instant;

public record UserStatusUpdateRequest(
    @PastOrPresent
    Instant newLastActiveAt
) {
}
