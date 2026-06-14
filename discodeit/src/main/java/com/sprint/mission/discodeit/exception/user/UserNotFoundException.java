package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.base.ErrorCode;
import com.sprint.mission.discodeit.exception.base.UserException;
import java.util.Map;
import java.util.UUID;



public class UserNotFoundException extends UserException {
  public UserNotFoundException(UUID userId) {
    super(ErrorCode.USER_NOT_FOUND, Map.of("ID", userId));
  }
}
