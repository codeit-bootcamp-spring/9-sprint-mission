package com.sprint.mission.discodeit.event.message;

import java.time.Instant;
import java.util.UUID;

public class UserDeletedEvent extends DeletedEvent<UUID> {

  public UserDeletedEvent(UUID userId, Instant deletedAt) {
    super(userId, deletedAt);
  }
}