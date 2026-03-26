package com.sprint.mission.discodeit.error;

import java.util.Map;

public class MessageException extends DiscodeitException {

  public MessageException(ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode, details);
  }

}
