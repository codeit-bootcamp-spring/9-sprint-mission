package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;


public class BinaryContentNotFoundException extends DiscodeitException {

  public BinaryContentNotFoundException(String id) {
    super(ErrorCode.BINARY_CONTENT_NOT_FOUND, id);
  }
}