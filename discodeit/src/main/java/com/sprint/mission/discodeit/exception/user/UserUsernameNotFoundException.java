package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserUsernameNotFoundException extends UserException {

  public UserUsernameNotFoundException(String username) {
    super(ErrorCode.USER_USERNAME_NOT_FOUND, username);
  }
}