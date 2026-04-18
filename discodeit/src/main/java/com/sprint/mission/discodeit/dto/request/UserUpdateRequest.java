package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateRequest(

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    @Size(min = 2, max = 10, message = "이름은 2~10자 사이여야합니다.")
    String newUsername,

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    String newEmail,

    @NotBlank(message = "패스워드는 필수 입력 값입니다.")
    @Size(min = 4, message = "패스워드는 4자리 이상이어야 합니다.")
    String newPassword
) {

}
