package com.sprint.mission.discodeit.event;

import java.util.UUID;

public record BinaryContentCreatedEvent(
    UUID binaryContentId,
    byte[] bytes
) {

  public BinaryContentCreatedEvent {
    bytes = bytes.clone();
  }

  @Override
  public byte[] bytes() {
    return bytes.clone();
  }
}
