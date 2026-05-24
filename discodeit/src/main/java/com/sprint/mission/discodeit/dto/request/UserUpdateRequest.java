package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "변경할 User 온라인 상태 정보")
public record UserUpdateRequest(
    @NotNull
    @Size(min = 6, max = 20)
    String newUsername,
    @NotNull
    @Email
    String newEmail,
    @NotNull
    @Size(min=4, max = 10)
    String newPassword
) {

}
