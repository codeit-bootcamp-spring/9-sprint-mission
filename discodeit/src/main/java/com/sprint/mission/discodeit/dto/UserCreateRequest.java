package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UserCreateRequest(
        String username,
        String email,
        String password,
        String phoneNumber,
        UUID profileId
) {}