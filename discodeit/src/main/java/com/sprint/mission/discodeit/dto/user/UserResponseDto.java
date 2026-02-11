package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String username,
        String email,
        boolean active
) {}
