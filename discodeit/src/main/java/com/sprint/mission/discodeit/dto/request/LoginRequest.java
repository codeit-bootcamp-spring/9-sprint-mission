package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank String username, // email에서 username으로 변경
    @NotBlank String password
) {

}