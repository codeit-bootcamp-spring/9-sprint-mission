package com.sprint.mission.discodeit.DTO.request;

import java.time.Instant;

public record UserStatusUpdateRequest(
        Instant newLastActiveAt
) {

}
