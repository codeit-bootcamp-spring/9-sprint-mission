package com.sprint.mission.discodeit.error;

import java.util.Map;

public class BinaryContentNotFoundException extends DiscodeitException {

  public BinaryContentNotFoundException(Map<String, Object> details) {
    super(ErrorCode.FILE_NOT_FOUND, details);
  }

}
