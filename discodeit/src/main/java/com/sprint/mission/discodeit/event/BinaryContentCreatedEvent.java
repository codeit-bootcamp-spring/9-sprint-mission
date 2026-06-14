package com.sprint.mission.discodeit.event;

import java.util.UUID;

public record BinaryContentCreatedEvent(
    UUID contentId,
    byte[] bytes
) {
}
