package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(name = "UserStatus")
public record UserStatusResponse(
    UUID userId,
    Instant lastActiveAt,
    Boolean online
) {

}