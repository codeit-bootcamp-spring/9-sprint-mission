package com.sprint.mission.discodeit.service.DTO;

import com.sprint.mission.discodeit.status.add.BinaryContent;

import java.util.UUID;

public record UserUpdateRequest(
        UUID userId,
        String username,
        String email,
        String password,
        BinaryContent profileImage
) { }
