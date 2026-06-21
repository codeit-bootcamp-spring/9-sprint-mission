package com.sprint.mission.discodeit.event.message;

import com.sprint.mission.discodeit.dto.data.UserDto;
import java.time.Instant;

public class UserDeletedEvent
    extends DeletedEvent<UserDto> {

  public UserDeletedEvent(
      UserDto data,
      Instant deletedAt
  ) {
    super(data, deletedAt);
  }
}