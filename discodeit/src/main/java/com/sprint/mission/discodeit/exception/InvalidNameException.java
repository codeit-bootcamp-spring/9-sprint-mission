package com.sprint.mission.discodeit.exception;

import java.util.Map;

public class InvalidNameException extends DiscodeitException {

  public InvalidNameException() {
    super(ErrorCode.INVALID_NAME, Map.of());
  }
}
