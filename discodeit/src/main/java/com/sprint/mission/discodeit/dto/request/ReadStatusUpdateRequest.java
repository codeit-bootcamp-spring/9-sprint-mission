package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record ReadStatusUpdateRequest(
    @NotNull Instant newLastReadAt   // lastReadAt -> newLastReadAt으로 변경
) {

}