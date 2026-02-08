package com.sprint.mission.discodeit.service.DTO;

import java.util.UUID;

public record ReadStatusCreateRequest(
        UUID userId,
        UUID channelId
) {
}
