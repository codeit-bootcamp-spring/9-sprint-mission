package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
    @NotNull @NotBlank
    @Schema(description = "사용자명", example = "스타벅스")
    String username,

    @NotNull @NotBlank
    @Schema(description = "비밀번호")
    String password
) {

}
