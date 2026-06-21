package com.sprint.mission.discodeit.event.message;

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.time.Instant;
import lombok.Getter;

@Getter
public class BinaryContentCreatedEvent extends CreatedEvent<BinaryContent> {

  private final byte[] bytes;

  public BinaryContentCreatedEvent(BinaryContent data, Instant createdAt, byte[] bytes) {
    super(data, createdAt);
    this.bytes = bytes;
  }
}
