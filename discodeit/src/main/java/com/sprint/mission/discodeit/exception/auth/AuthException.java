package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class AuthException extends DiscodeitException {

  public AuthException(ErrorCode errorCode, Object... args) {
    super(errorCode, args);
  }
}