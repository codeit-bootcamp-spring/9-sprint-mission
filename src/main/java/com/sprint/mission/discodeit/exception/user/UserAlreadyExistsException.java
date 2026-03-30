package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class UserAlreadyExistsException extends UserException {

  public UserAlreadyExistsException(String email) {
    super(
        ErrorCode.USER_EMAIL_DUPLICATED,
        Map.of("email", email)
    );
  }
}