package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String email,
        UserStatus userStatus
) {}
