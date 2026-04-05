package com.sprint.mission.discodeit.exception;

public abstract class MessageException extends DiscodeitException {
    protected MessageException(ErrorCode errorCode) {
        super(errorCode);
    }
}
