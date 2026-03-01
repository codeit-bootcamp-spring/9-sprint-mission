package com.sprint.mission.discordit.dto.request;

import java.time.Instant;
import java.util.UUID;

public record UserStatusCreateRequest(
    UUID userId,
    Instant lastActiveAt
) {

}
