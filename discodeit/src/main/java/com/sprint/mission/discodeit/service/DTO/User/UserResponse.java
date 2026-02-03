package com.sprint.mission.discodeit.service.DTO.User;

import java.util.UUID;

public record UserResponse(
        UUID userId,
        String username,
        String email,
        boolean online
) { }
