package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserAlreadyExistsException extends UserException {

  public UserAlreadyExistsException(String emailOrName) {
    super(ErrorCode.DUPLICATE_USER,
        String.format("이미 존재하는 사용자입니다: %s", emailOrName));
    addDetail("duplicateValue", emailOrName);
  }
}