package com.sprint.mission.discodeit.dto.request;

public record UserCreateRequest(
        String email,
        String username,
        String password
) {

}
