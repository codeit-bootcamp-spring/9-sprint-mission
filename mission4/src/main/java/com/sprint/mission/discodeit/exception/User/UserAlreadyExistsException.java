package com.sprint.mission.discodeit.exception.User;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class UserAlreadyExistsException extends UserException {

  public UserAlreadyExistsException(UUID userId) {
    super(ErrorCode.DUPLICATE_USER, Map.of("user_id", userId));
  }

  public UserAlreadyExistsException(String username) {
    super(ErrorCode.DUPLICATE_USER, Map.of("username", username));
  }
}
