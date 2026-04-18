package com.sprint.mission.discodeit.error;

import java.util.Map;

public class MessageNotFoundException extends MessageException {

  public MessageNotFoundException(Map<String, Object> details) {
    super(ErrorCode.MESSAGE_NOT_FOUND, details);
  }

}
