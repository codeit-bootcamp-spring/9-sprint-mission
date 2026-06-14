package com.sprint.mission.discodeit.event;

import java.util.UUID;
import lombok.Getter;

public class BinaryContentCreatedEvent {

  @Getter
  private final UUID binaryContentId;
  @Getter
  private final byte[] bytes;

  public BinaryContentCreatedEvent(UUID binaryContentId, byte[] bytes) {
    this.binaryContentId = binaryContentId;
    this.bytes = bytes;
  }
}

