package com.sprint.mission.discodeit.dto.userstatus;

import java.util.UUID;

public record UserStatusCreateRequest(
        Target target
) {
    public record Target(
            UUID userId
    ) {
    }
}