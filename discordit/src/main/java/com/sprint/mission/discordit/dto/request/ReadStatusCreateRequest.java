package com.sprint.mission.discordit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

public record ReadStatusCreateRequest(
    @Schema(description = "사용자 ID")
    UUID userId,
    @Schema(description = "채널 ID")
    UUID channelId,
    @Schema(description = "읽음 시간")
    Instant lastReadAt
) {

}
