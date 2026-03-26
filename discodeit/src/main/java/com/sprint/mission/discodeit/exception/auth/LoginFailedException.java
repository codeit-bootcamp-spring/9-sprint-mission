package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class LoginFailedException extends DiscodeitException {

  public LoginFailedException() {
    super(ErrorCode.AUTH_LOGIN_FAILED);
  }
}