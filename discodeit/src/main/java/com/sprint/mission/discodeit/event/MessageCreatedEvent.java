package com.sprint.mission.discodeit.event;

import java.util.UUID;

public record MessageCreatedEvent(
    UUID channelId,
    String channelName,
    UUID authorId,
    String authorUsername,
    String content
) {

}
