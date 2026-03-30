package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record ReadStatusCreateRequest(
    @NotNull(message = "userId는 필수입니다")
    UUID userId,

    @NotNull(message = "channelId는 필수입니다")
    UUID channelId,

    @NotNull(message = "lastReadAt은 필수입니다")
    Instant lastReadAt
) {

}
