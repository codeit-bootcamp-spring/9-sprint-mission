package com.sprint.mission.discodeit.exception.domain;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class UserAlreadyExistException extends UserException {
  public UserAlreadyExistException(String field, String value) {
    super(ErrorCode.USER_ALREADY_EXIST, Map.of(field, value));
  }
}