package com.sprint.mission.discodeit.error;

import java.util.Map;

public class UserException extends DiscodeitException {

  public UserException(ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode, details);
  }
}