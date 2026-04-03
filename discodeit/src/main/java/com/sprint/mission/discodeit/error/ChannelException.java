package com.sprint.mission.discodeit.error;

import java.util.Map;

public class ChannelException extends DiscodeitException {

  public ChannelException(ErrorCode errorCode) {
    super(errorCode);
  }

  public ChannelException(ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode, details);
  }
}