package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

public record UserUpdateRequest(
        UUID userId,
        UserUpdateParams user,
        ProfileImageParams profileImage
) {

}
