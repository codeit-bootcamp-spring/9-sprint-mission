package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    @Size(min = 2, max = 20)
    String newUsername,

    @Email
    String newEmail,

    @Size(min = 8)
    String newPassword
) {

}
