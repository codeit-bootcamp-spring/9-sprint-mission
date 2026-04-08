package com.sprint.mission.discodeit.exception;


public class ForbiddenException extends DiscodeitException {

  public ForbiddenException(String message) {
    super(ErrorCode.ACCESS_DENIED, message);
  }
}