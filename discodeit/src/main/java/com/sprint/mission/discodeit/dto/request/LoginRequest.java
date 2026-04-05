package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "아이디(또는 이메일)를 입력해주세요.")
    String username,

    @NotBlank(message = "비밀번호를 입력해주세요.")
    String password
) {

}
