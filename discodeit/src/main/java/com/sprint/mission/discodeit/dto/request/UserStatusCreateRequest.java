package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record UserStatusCreateRequest(
    @NotNull(message = "userId는 필수입니다.")
    UUID userId,

    @NotNull(message = "lastActiveAt은 필수입니다.")
    Instant lastActiveAt
) {

}
