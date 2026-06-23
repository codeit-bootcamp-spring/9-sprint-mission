package com.sprint.mission.discodeit.event.message;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import java.time.Instant;

public class ChannelUpdatedEvent extends UpdatedEvent<ChannelDto> {

  public ChannelUpdatedEvent(ChannelDto from, ChannelDto to) {
    super(from, to, Instant.now());
  }
}
