package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
    @NotBlank(message = "사용자 이름을 입력해주세요.")
    @Size(min = 2, max = 30, message = "사용자 이름은 2~30자 사이여야 합니다.")
    String username,

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    String email,

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, max = 50, message = "비밀번호는 8자 이상이어야 합니다.")
    String password
) {
}