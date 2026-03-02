package com.sprint.mission.discodeit.DTO.request;

public record UserCreateRequest(
        String username,
        String email,
        String password
) {

}
