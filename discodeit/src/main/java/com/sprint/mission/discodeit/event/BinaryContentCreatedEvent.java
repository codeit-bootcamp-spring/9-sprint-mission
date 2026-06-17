package com.sprint.mission.discodeit.event;

import java.util.UUID;

public record BinaryContentCreatedEvent(
    UUID binaryContentId,
    String fileName,
    String contentType,
    byte[] fileBytes
) {
}
