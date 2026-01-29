package com.sprint.mission.discodeit.DTO.UserService;

import java.time.Instant;

public record FindUserResponse(
        String name,
        String email,
        byte[] profileImageData,
        Instant lastLoginTime,
        boolean isOnline
) {
}
