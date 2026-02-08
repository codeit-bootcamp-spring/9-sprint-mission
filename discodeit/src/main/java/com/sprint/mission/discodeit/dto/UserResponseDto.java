package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String username,
        String email,
        boolean online,
        UUID profileImageId
) {}