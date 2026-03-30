package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    @NotBlank(message = "newUsername은 필수입니다")
    @Size(min = 2, max = 20, message = "newUsername은 2자 이상 20자 이하여야 합니다")
    String newUsername,

    @NotBlank(message = "newEmail은 필수입니다")
    @Email(message = "올바른 newEmail 형식이어야 합니다")
    String newEmail,

    @NotBlank(message = "newPassword는 필수입니다")
    @Size(min = 8, max = 100, message = "newPassword는 8자 이상 100자 이하여야 합니다")
    String newPassword
) {

}
