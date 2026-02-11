package com.sprint.mission.discodeit.DTO.UserService.Request;

public record CreateUserRequest(
        String name,
        String email,
        String password,
        byte[] profileImageData
) {

}
