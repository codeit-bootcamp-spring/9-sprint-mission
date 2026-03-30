package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
    @NotBlank(message = "username은 필수입니다")
    @Size(min = 2, max = 20, message = "username은 2자 이상 20자 이하여야 합니다")
    String username,

    @NotBlank(message = "password는 필수입니다")
    @Size(min = 8, max = 100, message = "password는 8자 이상 100자 이하여야 합니다")
    String password
) {

}
