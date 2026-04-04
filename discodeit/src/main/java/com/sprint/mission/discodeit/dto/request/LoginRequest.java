package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@Schema(description = "로그인 정보")
public record LoginRequest(
    @NotNull
    @Size(min = 6, max = 20)
    String username,
    @NotNull
    @Size(min = 6, max = 20)
    String password
    //, UUID profileId
) {
}
