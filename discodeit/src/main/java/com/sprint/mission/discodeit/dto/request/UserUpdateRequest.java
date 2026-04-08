package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;


public record UserUpdateRequest(

    @Size(min = 2, max = 50, message = "이름 변경 시 2자 이상, 50자 이하여야 합니다.")
    String newUsername,


    @Email(message = "올바른 이메일 형식이 아닙니다.")
    String newEmail,

    @Size(min = 8, message = "비밀번호 변경 시 최소 8자 이상이어야 합니다.")
    String newPassword
) {

}