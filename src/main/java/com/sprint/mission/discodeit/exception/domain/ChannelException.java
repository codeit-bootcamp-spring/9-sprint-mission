package com.sprint.mission.discodeit.exception.domain;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public abstract class ChannelException extends DiscodeitException {
  public ChannelException(ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode, (Throwable) details);
  }
}
