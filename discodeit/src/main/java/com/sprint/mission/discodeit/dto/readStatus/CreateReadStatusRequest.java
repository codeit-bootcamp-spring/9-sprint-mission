package com.sprint.mission.discodeit.dto.readStatus;

import java.util.UUID;

public record CreateReadStatusRequest(
        UUID channelId,
        UUID userId
) {
}
