package com.sprint.mission.discodeit.event;

import java.util.UUID;

public record MessageCreatedEvent(
    UUID messageId,
    UUID authorId,
    UUID channelId,
    String authorName,
    String channelName,
    String content
) {

}
