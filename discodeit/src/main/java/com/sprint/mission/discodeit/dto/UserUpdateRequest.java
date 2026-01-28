package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UpdateUserRequest(
        UUID id,
        String username,
        String email,
        String password,
        byte[] profileImage,
        String profileImageFileName)
{}
