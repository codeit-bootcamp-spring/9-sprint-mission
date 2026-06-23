package com.sprint.mission.discodeit.event.message;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import java.time.Instant;

public class BinaryContentUpdatedEvent
    extends UpdatedEvent<BinaryContentDto> {

  public BinaryContentUpdatedEvent(
      BinaryContentDto from,
      BinaryContentDto to,
      Instant updatedAt
  ) {
    super(from, to, updatedAt);
  }
}