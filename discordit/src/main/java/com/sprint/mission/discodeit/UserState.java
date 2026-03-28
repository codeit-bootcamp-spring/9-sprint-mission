package com.sprint.mission.discodeit;

import java.util.UUID;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class UserState {

  private String username = "";
  private UUID userId = null;

  public void userState(String username, UUID userId) {
    this.username = username;
    this.userId = userId;
  }

  public void userState(String username) {
    this.username = "";
    this.userId = null;
  }
}
