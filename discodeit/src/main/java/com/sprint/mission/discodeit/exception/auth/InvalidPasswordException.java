package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class InvalidPasswordException extends AuthException {

  public InvalidPasswordException(ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode, details);
  }
}
