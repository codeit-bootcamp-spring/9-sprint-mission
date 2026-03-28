package com.sprint.mission.discodeit.dto.request;

public record RequestUpdateUserDto(
    String newUsername, String newEmail, String newPassword) {

}