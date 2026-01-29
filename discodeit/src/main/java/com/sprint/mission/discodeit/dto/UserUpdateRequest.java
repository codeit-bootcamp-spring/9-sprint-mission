package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UserUpdateRequest(
        UUID id,
        String username,
        String email,
        String password,
        byte[] profileImage,
        String profileImageFileName)
{}
