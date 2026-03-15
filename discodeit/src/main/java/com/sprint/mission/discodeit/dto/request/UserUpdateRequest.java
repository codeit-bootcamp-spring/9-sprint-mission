package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;

public record UserUpdateRequest(
    String newUsername,
    String newEmail,
    String newPassword
) {

}