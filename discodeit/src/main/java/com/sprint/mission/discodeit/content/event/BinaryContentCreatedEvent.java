package com.sprint.mission.discodeit.content.event;

import java.util.UUID;

public class BinaryContentCreatedEvent {

  private final UUID binaryContentId;
  private final byte[] bytes;

  public BinaryContentCreatedEvent(UUID binaryContentId, byte[] bytes) {
    this.binaryContentId = binaryContentId;
    this.bytes = bytes;
  }

  public UUID getBinaryContentId() {
    return binaryContentId;
  }

  public byte[] getBytes() {
    return bytes;
  }
}
