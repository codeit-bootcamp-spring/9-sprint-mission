package com.sprint.mission.discodeit.event;

import java.util.Arrays;
import java.util.UUID;

public record BinaryContentCreatedEvent(
    UUID binaryContentId,
    byte[] bytes
) {

  public BinaryContentCreatedEvent {
    bytes = Arrays.copyOf(bytes, bytes.length);
  }
}
