package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class InvalidTokenException extends DiscodeitException {

  public InvalidTokenException() {
    super(ErrorCode.INVALID_TOKEN);
  }

  public InvalidTokenException(Throwable cause) {
    super(ErrorCode.INVALID_TOKEN, cause);
  }
}
