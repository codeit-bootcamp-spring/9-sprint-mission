package com.sprint.mission.discodeit.exception;

import java.util.List;

public class UserNotFoundException extends UserException {

  public UserNotFoundException() {
    super(ErrorCode.USER_NOT_FOUND);
  }

  // Map 대신 List<ErrorDetail>을 받도록 수정
  public UserNotFoundException(List<ErrorDetail> details) {
    super(ErrorCode.USER_NOT_FOUND, details);
  }
}