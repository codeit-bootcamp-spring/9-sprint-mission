package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(

    @NotBlank(message = "username은 필수입니다.")
    @Size(min = 2, max = 20, message = "username은 2~20자여야 합니다.")
    String newUsername,


    @NotBlank(message = "email은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    String newEmail,

    @NotBlank(message = "password는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상입니다.")
    String newPassword
) {

}
