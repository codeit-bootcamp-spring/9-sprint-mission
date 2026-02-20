package com.sprint.mission.discodeit.dto.user;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String username,
    String email,
    UUID profileId,
    Boolean online
) { }
