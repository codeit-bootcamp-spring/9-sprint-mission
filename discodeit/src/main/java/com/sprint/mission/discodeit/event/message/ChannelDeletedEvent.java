package com.sprint.mission.discodeit.event.message;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import java.time.Instant;

public class ChannelDeletedEvent
    extends DeletedEvent<ChannelDto> {

  public ChannelDeletedEvent(
      ChannelDto data,
      Instant deletedAt
  ) {
    super(data, deletedAt);
  }
}