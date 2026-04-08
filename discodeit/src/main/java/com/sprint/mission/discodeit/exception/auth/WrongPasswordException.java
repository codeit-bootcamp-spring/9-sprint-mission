package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class WrongPasswordException extends AuthException {

  public WrongPasswordException() {
    super(ErrorCode.WRONG_PASSWORD);
  }
}