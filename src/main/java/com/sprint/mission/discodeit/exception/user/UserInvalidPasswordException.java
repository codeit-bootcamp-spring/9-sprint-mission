package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class UserInvalidPasswordException extends UserException {

  public UserInvalidPasswordException(UUID userId) {
    super(
        ErrorCode.USER_INVALID_PASSWORD,
        Map.of("userId", userId)
    );
  }

  public UserInvalidPasswordException(String username) {
    super(
        ErrorCode.USER_INVALID_PASSWORD,
        Map.of("username", username)
    );
  }
}

