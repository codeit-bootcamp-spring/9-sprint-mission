package com.sprint.mission.discodeit.exception.security;


import com.sprint.mission.discodeit.exception.base.DiscodeitException;
import com.sprint.mission.discodeit.exception.base.ErrorCode;
import java.util.Collections;

public class NotExistRefreshTokenException extends DiscodeitException {

  public NotExistRefreshTokenException() {
    super(ErrorCode.NOT_EXIST_REFRESH_TOKEN, Collections.emptyMap());
  }
}
