package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record ReadStatusUpdateRequest(
    @NotNull(message = "newLastReadAt은 필수입니다.")
    Instant newLastReadAt
) {

}
