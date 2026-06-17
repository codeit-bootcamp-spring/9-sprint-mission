package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;


@Builder
public record UserCreateRequest(
    @NotBlank(message = "유저 이름은 필수입니다.")
    @Size(min = 1, max = 100, message = "유저 이름은 1-100자이어야합니다.")
    String username,
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Size(max = 100, message = "이메일은 100자이내여야 합니다.")
    String email,

    @NotBlank(message = "비밀번호는 필수여야합니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야합니다.")
    String password
) {

}
