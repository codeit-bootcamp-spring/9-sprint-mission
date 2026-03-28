package com.sprint.mission.discodeit.dto.response;

import java.util.List;
import java.util.UUID;

public record ResponseMessageDto(
        UUID messageId,
        UUID channelId,
        UUID userId,
        List<UUID> binaryContentIds,
        String createAt,
        String updateAt,
        String content
) {
}
