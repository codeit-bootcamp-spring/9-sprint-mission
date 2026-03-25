package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class WrongPasswordException extends AuthException {

  public WrongPasswordException(String password) {
    super(ErrorCode.WRONG_PASSWORD, Map.of("password", password));
  }

}
