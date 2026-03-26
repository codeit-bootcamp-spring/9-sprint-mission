package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
    @NotBlank(message = "유저 이름은 필수입니다.")
    @Size(min = 1, max = 100, message = "유저 이름은 최소 1자 이상 최대 100자 이내이어야합니다.")
    String username,
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 1, max = 100, message = "비밀번호는 최소 1자 이상 최대 100자 이내이어야합니다")
    String password
) {

}
