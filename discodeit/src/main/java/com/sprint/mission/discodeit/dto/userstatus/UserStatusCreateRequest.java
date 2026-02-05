package com.sprint.mission.discodeit.dto.userstatus;

import java.util.UUID;

/// UserStatus create request DTO.
public record UserStatusCreateRequest(
        Target target
) {
    public record Target(
            UUID userId
    ) {
    }
}