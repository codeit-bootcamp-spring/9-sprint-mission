package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public abstract class BinaryContentException extends DiscodeitException {

  protected BinaryContentException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }

  protected BinaryContentException(ErrorCode errorCode, String message, Throwable cause) {
    super(errorCode, message, cause);
  }
}