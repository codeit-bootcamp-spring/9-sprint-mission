package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
        // 빈 문자열 방지 및 길이 제한
        @NotBlank(message = "Username은 필수입니다.")
        @Size(min = 2, max = 50, message = "Username은 2자 이상 50자 이하이어야 합니다.")
        String username,

        // 빈 문자열 방지 및 이메일 형식 검사
        @NotBlank(message = "Email은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        // 빈 문자열 방지 및 길이 제한
        @NotBlank(message = "Password는 필수입니다.")
        @Size(min = 8, max = 60, message = "Password는 8자 이상 60자 이하이어야 합니다.")
        String password
) {
}