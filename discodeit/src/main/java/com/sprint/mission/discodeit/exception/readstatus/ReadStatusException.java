package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class ReadStatusException extends DiscodeitException {

  public ReadStatusException(ErrorCode errorCode, Object... args) {
    super(errorCode, args);
  }
}