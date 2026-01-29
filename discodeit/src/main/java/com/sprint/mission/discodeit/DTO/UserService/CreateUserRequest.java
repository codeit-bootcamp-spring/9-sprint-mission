package com.sprint.mission.discodeit.DTO.UserService;

import java.util.UUID;

public record CreateUserRequest(
        String name,
        String email,
        String password,
        Byte[] profileImageData
) {

}
