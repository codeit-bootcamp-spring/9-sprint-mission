package com.sprint.mission.discodeit.dto.request;

import java.util.List;
import java.util.UUID;

public record RequestCreateMessageDto(
        String text,
        UUID channelId,
        UUID userId,
        List<UUID> binaryContentIds
) {
}
