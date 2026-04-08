package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(

    @Size(min = 2, max = 20, message = "username은 2~20자입니다.")
    String newUsername,

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    String newEmail,

    @Size(min = 6, message = "password는 최소 6자 이상입니다.")
    String newPassword

) {

}