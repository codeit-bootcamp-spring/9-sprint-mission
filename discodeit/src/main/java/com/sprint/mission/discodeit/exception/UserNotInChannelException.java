package com.sprint.mission.discodeit.exception;

public class UserNotInChannelException extends ChannelException {
  public UserNotInChannelException() {
    super(ErrorCode.USER_NOT_IN_CHANNEL);
  }

}
