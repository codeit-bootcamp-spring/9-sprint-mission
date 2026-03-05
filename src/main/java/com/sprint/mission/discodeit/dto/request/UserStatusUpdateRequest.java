package com.sprint.mission.discodeit.dto.request;

import java.time.Instant;
import lombok.Getter;

@Getter
public class UserStatusUpdateRequest {

  private Instant newLastActiveAt;

  public UserStatusUpdateRequest() {}

  public void setNewLastActiveAt(Instant newLastActiveAt) {
    this.newLastActiveAt = newLastActiveAt;
  }
}