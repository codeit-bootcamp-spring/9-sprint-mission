package com.sprint.mission.discodeit.DTO.UserService.Request;

import java.util.UUID;

public record UpdateUserRequest(
        UUID userId,
        String newName,
        String newPassword,
        String newEmail,
        byte[] newProfileImageData
) {
}
