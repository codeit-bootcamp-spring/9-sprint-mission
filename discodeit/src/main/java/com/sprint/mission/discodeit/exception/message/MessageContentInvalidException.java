package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class MessageContentInvalidException extends MessageException{

  public MessageContentInvalidException(ErrorCode errorCode,
      Map<String, Object> details) {
    super(errorCode, details);
  }

  public MessageContentInvalidException() {
    super(ErrorCode.MESSAGE_CONTENT_INVALID);
  }
}
