package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

public record UserUpdateRequest(
        UUID userId,
        Params params
) {
    public record Params(
            UserFields user,
            ProfileImageParams profileImage
    ) {
    }

    public record UserFields(
            String displayName,
            String email,
            String phoneNumber
    ) {
    }
}