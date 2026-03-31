package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserStatusNotFoundException extends UserException {

  public UserStatusNotFoundException(String userId) {
    super(ErrorCode.USER_STATUS_NOT_FOUND, userId);
  }
}