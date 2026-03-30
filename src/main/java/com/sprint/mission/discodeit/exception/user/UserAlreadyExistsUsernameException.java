package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class UserAlreadyExistsUsernameException extends UserException {

  public UserAlreadyExistsUsernameException(String username) {
    super(
        ErrorCode.USER_USERNAME_DUPLICATED,
        Map.of("username", username)
    );
  }
}

