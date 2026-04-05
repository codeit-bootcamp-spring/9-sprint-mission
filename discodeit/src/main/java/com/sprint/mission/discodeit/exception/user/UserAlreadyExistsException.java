package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

//진짜 사용하는 구체적인 예외
public class UserAlreadyExistsException extends UserException {

  public UserAlreadyExistsException(String email) {
    super(ErrorCode.DUPLICATE_USER, Map.of("email", email)
    );
  }

}
