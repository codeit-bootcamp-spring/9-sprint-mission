package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    @Size(min = 2, max = 20, message = "Username must be between 2 and 20 characters")
    String newUsername,

    @Email(message = "Invalid email format")
    String newEmail,

    @Size(min = 8, message = "Password must be at least 8 characters long")
    String newPassword
) {
}
