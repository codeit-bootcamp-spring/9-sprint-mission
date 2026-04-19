package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.base.ErrorCode;
import com.sprint.mission.discodeit.exception.base.UserException;
import java.util.Map;

public class UserAlreadyExistsException extends UserException {

  public static UserAlreadyExistsException email(String email) {
    return new UserAlreadyExistsException("email", email);
  }

  public static UserAlreadyExistsException username(String username) {
    return new UserAlreadyExistsException("username", username);
  }

  private UserAlreadyExistsException(String field, String value) {
    super(ErrorCode.DUPLICATE_USER, Map.of(
        "field", field,
        "value", value
    ));
  }
}
