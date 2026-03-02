package com.sprint.mission.discodeit.DTO.request;

public record UserUpdateRequest(
        String newUsername,
        String newEmail,
        String newPassword
) {

}
