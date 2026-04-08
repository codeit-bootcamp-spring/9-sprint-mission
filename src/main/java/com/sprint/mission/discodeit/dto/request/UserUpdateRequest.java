package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    String newUsername,
    @Email String newEmail,
    @Size(min = 8) String newPassword
) {

}
