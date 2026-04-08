package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class UserStatusNotFoundException extends DiscodeitException {

  public UserStatusNotFoundException(UUID id) {
    super(
        ErrorCode.USER_STATUS_NOT_FOUND,
        Map.of("userStatusId", id)
    );
  }
}