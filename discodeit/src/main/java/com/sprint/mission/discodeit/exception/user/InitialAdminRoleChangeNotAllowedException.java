package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class InitialAdminRoleChangeNotAllowedException extends UserException {

  public InitialAdminRoleChangeNotAllowedException(Map<String, Object> details) {
    super(ErrorCode.INITIAL_ADMIN_ROLE_CHANGE_NOT_ALLOWED, details);
  }
}
