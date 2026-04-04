package com.sprint.mission.discodeit.exception.userStatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class UserStatusInavalidException extends UserStatusException{

  public UserStatusInavalidException(ErrorCode errorCode,
      Map<String, Object> details) {
    super(errorCode, details);
  }

  public UserStatusInavalidException() {
    super(ErrorCode.USER_STATUS_INVALID);
  }
}
