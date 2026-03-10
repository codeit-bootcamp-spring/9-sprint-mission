package com.sprint.mission.discodeit.dto.request;

import java.time.Instant;
import jakarta.validation.constraints.NotNull;

public record UserStatusUpdateRequest(
    @NotNull Instant newLastActiveAt // statusDescription -> newLastActiveAt으로 변경
) {

}