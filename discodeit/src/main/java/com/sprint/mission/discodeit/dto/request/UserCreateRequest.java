package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserCreateRequest(
    @NotNull @NotBlank
    @Schema(description = "사용자명")
    String username,

    @NotNull @NotBlank @Email
    @Schema(description = "이메일", example = "email@google.com")
    String email,

    @NotNull @NotBlank
    @Schema(description = "비밀번호")
    String password
) {

}
