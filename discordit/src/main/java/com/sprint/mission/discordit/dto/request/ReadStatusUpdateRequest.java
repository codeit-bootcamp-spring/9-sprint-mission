package com.sprint.mission.discordit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

public record ReadStatusUpdateRequest(
    @Schema(description = "수정할 시간")
    Instant newLastReadAt
) {

}
