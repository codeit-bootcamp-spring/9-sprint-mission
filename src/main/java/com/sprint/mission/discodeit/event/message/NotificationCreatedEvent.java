package com.sprint.mission.discodeit.event.message;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import java.time.Instant;

public class NotificationCreatedEvent extends CreatedEvent<NotificationDto> {

  public NotificationCreatedEvent(
      NotificationDto data,
      Instant createdAt
  ) {
    super(data, createdAt);
  }
}