package com.sprint.mission.discodeit.exception;

public abstract class UserException extends DiscodeitException {
    protected UserException(ErrorCode errorCode) {
        super(errorCode);
    }
}
