package com.sprint.mission.discodeit.dto.user;

public record UserUpdateParams(
        String displayName,
        String email,
        String phoneNumber
) {

}