package com.sprint.mission.discodeit.exception.ReadStatus;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class ReadStatusException extends DiscodeitException {

  public ReadStatusException(ErrorCode errorCode) {
    super(errorCode);
  }

  public ReadStatusException(ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode, details);
  }

}
