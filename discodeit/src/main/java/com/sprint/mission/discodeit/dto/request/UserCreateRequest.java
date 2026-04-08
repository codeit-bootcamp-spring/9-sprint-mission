package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
    @NotBlank(message = "username은 필수입니다.")
    @Size(min = 2, max = 15, message = "username은 2~15자입니다.")
    String username,
    @NotBlank(message = "email은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    String email,
    @NotBlank(message = "password는 필수입니다.")
    @Size(min = 6, message = "password는 최소 6자 이상입니다.")
    String password
) {

}
