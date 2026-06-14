package com.sprint.mission.discodeit.exception.base;

import java.util.Map;

public abstract class NotificationException extends DiscodeitException {

  public NotificationException(ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode, details);
  }
}
