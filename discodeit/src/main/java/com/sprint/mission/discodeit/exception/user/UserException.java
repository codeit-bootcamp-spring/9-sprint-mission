package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

//껍데기 이름표 느낌
public class UserException extends DiscodeitException {

  public UserException(ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode, details);
  }
}
