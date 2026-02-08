package com.sprint.mission.discodeit.service.DTO.User;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.UUID;

public record UserUpdateRequest(
        UUID userId,
        String username,
        String email,
        String password,
        BinaryContent profileImage
) { }
