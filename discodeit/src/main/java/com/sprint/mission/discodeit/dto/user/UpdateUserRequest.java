package com.sprint.mission.discodeit.dto.user;


public record UpdateUserRequest(
        String newName,
        String newEmail,
        String newPassword
) {
}
