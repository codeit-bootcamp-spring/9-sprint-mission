package com.sprint.mission.discodeit.exception.User;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;


public class UserNotFoundException extends UserException {

  public UserNotFoundException(UUID userId) {
    super(ErrorCode.USER_NOT_FOUND, Map.of("user_id", userId));
  }

  public UserNotFoundException(String username) {
    super(ErrorCode.USER_NOT_FOUND, Map.of("username", username));
  }
}
