package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class UserAlreadyExistException extends UserException {

  public UserAlreadyExistException(Map<String, Object> details) {
    super(ErrorCode.DUPLICATE_USER, details);
  }
}

