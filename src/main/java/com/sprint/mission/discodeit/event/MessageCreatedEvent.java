package com.sprint.mission.discodeit.event;

import java.util.UUID;

public record MessageCreatedEvent(
        UUID userId,
        String userName,
        UUID channelId,
        String channelName,
        UUID messageId,
        String content
) {
}
