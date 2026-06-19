package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class SelfRoleChangeNotAllowedException extends UserException {

  public SelfRoleChangeNotAllowedException(Map<String, Object> details) {
    super(ErrorCode.SELF_ROLE_CHANGE_NOT_ALLOWED, details);
  }
}
