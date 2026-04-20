package com.sprint.mission.discodeit.exception;

import java.util.List;

public class UserAlreadyExistsException extends UserException {

  public UserAlreadyExistsException() {
    super(ErrorCode.DUPLICATE_USER);
  }

  // Map 대신 List<ErrorDetail>을 받도록 수정
  public UserAlreadyExistsException(List<ErrorDetail> details) {
    super(ErrorCode.DUPLICATE_USER, details);
  }
}