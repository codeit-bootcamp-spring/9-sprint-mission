package com.sprint.mission.discodeit.DTO.UserService;

import java.util.UUID;

public record UpdateUserRequest(
        UUID userId,
        String newName,
        String newPassword,
        String newEmail,
        Byte[] newProfileImageData
) {
}
