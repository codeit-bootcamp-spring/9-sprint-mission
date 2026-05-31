package com.sprint.mission.discodeit.entity.event;

import java.util.UUID;

public record MessageCreatedEvent(
    UUID channelId,
    UUID authorId,
    String content

) {

}
