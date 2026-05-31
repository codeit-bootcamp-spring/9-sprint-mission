package com.sprint.mission.discodeit.dto.request;


import jakarta.validation.constraints.*;

public record UserCreateRequest(
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    String email,
    @NotBlank(message = "사용자 이름은 필수입니다.")
    @Size(min = 2, max = 10, message = "사용자 이름은 2자 이상 10자 이하여야 합니다.")
    String username,
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 4, max = 15, message = "비밀번호는 4자 이상 15자 이하여야 합니다.")
    String password
) {

}
