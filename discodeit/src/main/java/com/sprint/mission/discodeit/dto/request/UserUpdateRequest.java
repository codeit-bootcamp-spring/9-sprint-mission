package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;

public record UserUpdateRequest(
    String newUsername,
    String newEmail, // 값이 들어올 때만 이메일 형식 검사!
    String newPassword
) {

}