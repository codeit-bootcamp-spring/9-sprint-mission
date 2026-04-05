package com.sprint.mission.discodeit.exception;

public abstract class ChannelException extends DiscodeitException {
    protected ChannelException(ErrorCode errorCode) {
        super(errorCode);
    }
}
