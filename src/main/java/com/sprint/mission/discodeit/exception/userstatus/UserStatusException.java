package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserStatusException extends DiscodeitException {

    public UserStatusException(ErrorCode errorCode) {
        super(errorCode);
    }
}