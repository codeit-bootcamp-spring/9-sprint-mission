package com.sprint.mission.discodeit.exception.security;

import com.sprint.mission.discodeit.exception.base.DiscodeitException;
import com.sprint.mission.discodeit.exception.base.ErrorCode;
import java.util.Collections;

public class FailUpdateRefreshTokenException extends DiscodeitException {

  public FailUpdateRefreshTokenException() {
    super(ErrorCode.FAIL_UPDATE_REFRESH_TOKEN, Collections.emptyMap());
  }
}
