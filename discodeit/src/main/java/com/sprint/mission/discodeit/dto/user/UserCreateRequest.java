package com.sprint.mission.discodeit.dto.user;

public record UserCreateRequest(
        String email,
        String username,
        String password
) {

}
