package com.sprint.mission.discordit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
    @Schema(description = "사용자명", example = "스타벅스")
    String username,

    @Schema(description = "비밀번호")
    String password
) {

}
