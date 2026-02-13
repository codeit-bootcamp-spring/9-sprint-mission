package com.sprint.mission.discodeit.dto.readstatus;

import java.util.UUID;

public record ReadStatusCreateRequest(
        Target target
) {
    public record Target(
            UUID channelId,
            UUID userId
    ) {}
}
