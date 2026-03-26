package com.sprint.mission.discodeit.error;

import java.util.Map;

public class UserNotFoundException extends UserException {

  public UserNotFoundException(Map<String, Object> details) {
    super(ErrorCode.USER_NOT_FOUND, details);
  }
}