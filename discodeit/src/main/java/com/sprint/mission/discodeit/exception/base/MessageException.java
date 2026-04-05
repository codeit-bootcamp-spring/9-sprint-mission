package com.sprint.mission.discodeit.exception.base;

import java.util.Map;

public abstract class MessageException extends DiscodeitException {
  protected MessageException(ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode, details);
  }
}