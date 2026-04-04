package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "User 생성 정보")
public record UserCreateRequest(
    @NotNull
    @Size(min = 6, max = 20)
    String username,
    @NotNull
    @Email
    String email,
    @NotNull
    @Size(min=4, max = 10)
    String password
) {

}
