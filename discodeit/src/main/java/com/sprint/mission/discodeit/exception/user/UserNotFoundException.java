package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.UUID;

public class UserNotFoundException extends UserException {

  public UserNotFoundException(UUID userId) {
    super(ErrorCode.USER_NOT_FOUND,
        String.format("사용자를 찾을 수 없습니다: ID=%s", userId));
    addDetail("userId", userId);
    addDetail("searchType", "byId");
  }

  public UserNotFoundException(String name) {
    super(ErrorCode.USER_NOT_FOUND,
        String.format("사용자를 찾을 수 없습니다: Name=%s", name));
    addDetail("name", name);
    addDetail("searchType", "byName");
  }
}