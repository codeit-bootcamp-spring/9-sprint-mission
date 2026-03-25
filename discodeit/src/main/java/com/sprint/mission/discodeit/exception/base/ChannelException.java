package com.sprint.mission.discodeit.exception.base;

import java.util.Map;

public abstract class ChannelException extends DiscodeitException {
  protected ChannelException(ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode, details);
  }
}