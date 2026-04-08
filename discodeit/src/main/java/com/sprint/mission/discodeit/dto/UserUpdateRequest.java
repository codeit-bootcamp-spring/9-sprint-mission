package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(

    @NotBlank(message = "유저네임은 필수 입력값입니다.")
    @Size(min = 2, max = 20, message = "유저네임은 2자 이상, 20자 이하여야 합니다.")
    String newUsername,

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    String newEmail,

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    String newPassword
) {

}