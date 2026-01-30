package com.sprint.mission.discodeit.service.DTO;

import java.util.UUID;

public record UserResponse(
        UUID userId,
        String username,
        String email,
        boolean online
) { }
