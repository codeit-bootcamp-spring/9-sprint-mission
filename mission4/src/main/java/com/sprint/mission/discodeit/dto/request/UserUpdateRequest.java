package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;


@Builder
public record UserUpdateRequest(
    @Size(min = 2, max = 100, message = "이름은 최소 2자 이상 최대 100자 이하여야합니다.")
    String newUsername,
    @Email(message = "잘못된 이메일 형식입니다.")
    @Size(min = 2, max = 100, message = "이메일은 최소 2자 이상 최대 100자 이하여야합니다.")
    String newEmail,
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야합니다.")
    String newPassword
) {

}
