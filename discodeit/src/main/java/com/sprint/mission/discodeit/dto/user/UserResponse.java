package com.sprint.mission.discodeit.dto.user;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String email,
        UUID profileId,
        Instant lastLoginTime,
        boolean online
) { }
