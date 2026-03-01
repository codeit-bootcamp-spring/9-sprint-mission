package com.sprint.mission.discordit.dto.request;

import java.time.Instant;

public record UserStatusUpdateRequest(
    Instant newLastActiveAt
) {

}
