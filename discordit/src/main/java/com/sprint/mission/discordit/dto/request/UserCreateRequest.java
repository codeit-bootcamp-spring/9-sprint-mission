package com.sprint.mission.discordit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserCreateRequest(
    @Schema(description = "사용자명")
    String username,

    @Schema(description = "이메일", example = "email@google.com")
    String email,

    @Schema(description = "비밀번호")
    String password
) {

}
