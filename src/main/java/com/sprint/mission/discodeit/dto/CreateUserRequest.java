package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record CreateUserRequest(
        String username,
        String email,
        String password,
        UUID profileImageId
) {}
