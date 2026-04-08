package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class UserStatusAlreadyExistsException extends DiscodeitException {

  public UserStatusAlreadyExistsException(UUID userId) {
    super(
        ErrorCode.USER_STATUS_ALREADY_EXISTS,
        Map.of("userId", userId)
    );
  }
}