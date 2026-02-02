package com.sprint.mission.discodeit.DTO.ReadStatusService;

import java.util.UUID;

public record CreateReadStatusRequest(
        UUID channelId,
        UUID userId
) {
}
