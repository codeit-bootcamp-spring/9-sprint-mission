package com.sprint.mission.discodeit.exception.base;

import java.util.Map;

public abstract class UserException extends DiscodeitException {
  protected UserException(ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode, details);
  }
}
