package com.sprint.mission.discodeit.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    @Size(min = 2, max = 10, message = "사용자 이름은 2자 이상 10자 이하여야 합니다.")
    String newUsername,
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    String newEmail,
    @Size(min = 4, max = 15, message = "비밀번호는 4자 이상 15자 이하여야 합니다.")
    String newPassword
) {
}
