package com.sprint.mission.discodeit.service.checker;

import com.sprint.mission.discodeit.UserState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckService {

  private final UserState userState;

  public boolean isNotLogin() {
    return userState.getUsername() == null || userState.getUsername().isEmpty();
  }
}
