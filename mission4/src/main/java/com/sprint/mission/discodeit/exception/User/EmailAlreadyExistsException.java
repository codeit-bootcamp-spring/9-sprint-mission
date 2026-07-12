package com.sprint.mission.discodeit.exception.User;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

public class EmailAlreadyExistsException extends UserException {

  public EmailAlreadyExistsException(String email) {
    super(ErrorCode.DUPLICATE_EMAIL, Map.of("email", email));
  }
}
