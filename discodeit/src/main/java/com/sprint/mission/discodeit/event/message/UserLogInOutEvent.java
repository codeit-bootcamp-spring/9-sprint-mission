package com.sprint.mission.discodeit.event.message;

import java.util.UUID;

public class UserLogInOutEvent {

  private final UUID userId;
  private final boolean isLogin;

  public UserLogInOutEvent(UUID userId, boolean isLogin) {
    this.userId = userId;
    this.isLogin = isLogin;
  }

  public UUID getUserId() {
    return userId;
  }

  public boolean isLogin() {
    return isLogin;
  }
}
