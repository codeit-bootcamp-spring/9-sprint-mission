package com.sprint.mission.discodeit.event.message;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import java.time.Instant;

public class MessageCreatedEvent extends CreatedEvent<MessageDto> {

  public MessageCreatedEvent(MessageDto data, Instant createdAt) {
    super(data, createdAt);
  }
}
