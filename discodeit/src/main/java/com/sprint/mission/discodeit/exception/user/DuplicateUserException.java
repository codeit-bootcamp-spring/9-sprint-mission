package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class DuplicateUserException extends UserException { // 실제 코드에서 예외 클래스로 감싸서 던짐

  public DuplicateUserException(String email) {
    super(
        ErrorCode.DUPLICATE_USER,
        Map.of("email", email)
    );
  }
}