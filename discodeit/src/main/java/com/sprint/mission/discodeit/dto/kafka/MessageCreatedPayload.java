package com.sprint.mission.discodeit.dto.kafka;

import java.util.UUID;

public record MessageCreatedPayload(
    UUID messageId, UUID authorId, UUID channelId, String authorName, String channelName, String content
) {

}
