package com.sprint.mission.discodeit.entity.event;

import java.util.UUID;

public record BinaryContentCreatedEvent(
    UUID binaryContentId,
    byte[] bytes
) {

}
