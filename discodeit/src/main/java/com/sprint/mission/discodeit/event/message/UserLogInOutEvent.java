package com.sprint.mission.discodeit.event.message;

import java.util.UUID;
import lombok.Getter;

@Getter
public class UserLogInOutEvent {

  private final UUID userId;
  private final boolean loggedIn;

  public UserLogInOutEvent(UUID userId, boolean loggedIn) {
    this.userId = userId;
    this.loggedIn = loggedIn;
  }
}
