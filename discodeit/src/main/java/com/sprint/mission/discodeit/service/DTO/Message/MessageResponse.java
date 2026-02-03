package com.sprint.mission.discodeit.service.DTO.Message;

import java.time.Instant;
import java.util.UUID;

public record MessageResponse(
        UUID id,
        UUID channelId,
        UUID authorId,
        String content,
        Instant creatAt
) {
}
