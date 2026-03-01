package com.sprint.mission.discordit.dto.request;

public record UserUpdateRequest(
    String newUsername,
    String newEmail,
    String newPassword
) {

}
