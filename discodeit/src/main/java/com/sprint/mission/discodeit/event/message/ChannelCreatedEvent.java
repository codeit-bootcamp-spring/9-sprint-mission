package com.sprint.mission.discodeit.event.message;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import java.time.Instant;

public class ChannelCreatedEvent extends CreatedEvent<ChannelDto> {

  public ChannelCreatedEvent(
      ChannelDto data,
      Instant createdAt
  ) {
    super(data, createdAt);
  }
}