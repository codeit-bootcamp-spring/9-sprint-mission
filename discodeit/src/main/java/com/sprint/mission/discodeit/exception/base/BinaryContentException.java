package com.sprint.mission.discodeit.exception.base;

import java.util.Map;

public abstract class BinaryContentException extends DiscodeitException {
  protected BinaryContentException(ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode, details);
  }
}
