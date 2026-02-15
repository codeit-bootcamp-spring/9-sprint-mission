package com.sprint.mission.discodeit.dto.user;

public record CreateUserRequest(
        String name,
        String email,
        String password
) {

}
