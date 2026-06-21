package com.sprint.mission.discodeit.event.message;

import java.time.Instant;
import lombok.Getter;

@Getter
public abstract class DeletedEvent<T> {

  private final T data;
  private final Instant deletedAt;

  protected DeletedEvent(final T data, final Instant deletedAt) {
    this.data = data;
    this.deletedAt = deletedAt;
  }
}
