package com.sprint.mission.discodeit.dto.response;

import java.util.UUID;

public record UserResponse(
    UUID id,
    String username,
    String email,
    BinaryContentResponse profile,
    Boolean online
) {

}

