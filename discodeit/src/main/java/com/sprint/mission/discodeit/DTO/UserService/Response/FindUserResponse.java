package com.sprint.mission.discodeit.DTO.UserService.Response;

import java.time.Instant;
import java.util.UUID;

public record FindUserResponse(
        UUID id,
        String name,
        String email,
        UUID profileImageId,
        Instant lastLoginTime,
        boolean isOnline
) { }
