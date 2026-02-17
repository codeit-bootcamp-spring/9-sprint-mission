package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UpdateUserRequest(
        String username,
        String email,
        String password,
        UUID profileImageId
) {}
