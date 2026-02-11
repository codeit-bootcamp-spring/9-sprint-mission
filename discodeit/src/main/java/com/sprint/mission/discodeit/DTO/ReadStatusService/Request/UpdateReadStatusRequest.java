package com.sprint.mission.discodeit.DTO.ReadStatusService.Request;

import java.time.Instant;
import java.util.UUID;

public record UpdateReadStatusRequest(
        UUID id,
        UUID channelId,
        UUID userId,
        Instant lastReadAt
) {
}
