package com.sprint.mission.discodeit.exception;

public class UnauthorizedMessageAccessException extends MessageException {
  public UnauthorizedMessageAccessException() {
    super(ErrorCode.UNAUTHORIZED_MESSAGE_ACCESS);
  }

}
