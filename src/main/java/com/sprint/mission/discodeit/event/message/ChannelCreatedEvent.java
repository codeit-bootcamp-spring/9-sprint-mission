package com.sprint.mission.discodeit.event.message;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import java.time.Instant;
import lombok.Getter;

@Getter
public class ChannelCreatedEvent extends CreatedEvent<ChannelDto> {

  public ChannelCreatedEvent(ChannelDto data) {
    super(data, Instant.now());
  }
}