package com.sprint.mission.discodeit.exception;

public class DuplicateUserException extends UserException {
    public DuplicateUserException() {
        super(ErrorCode.DUPLICATE_USER);
    }
}
