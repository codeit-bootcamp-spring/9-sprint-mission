package com.sprint.mission.discodeit.exception.User;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


public abstract class UserException extends DiscodeitException {

  protected UserException(ErrorCode errorCode) {
    super(errorCode);
  }

  protected UserException(ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode, details);
  }

}
