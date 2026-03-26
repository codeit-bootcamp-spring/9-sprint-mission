package com.sprint.mission.discodeit.error;

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.Map;

public class BinaryContentException extends DiscodeitException {

  public BinaryContentException(ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode, details);
  }

}
