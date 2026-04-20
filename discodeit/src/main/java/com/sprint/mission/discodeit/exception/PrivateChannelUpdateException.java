package com.sprint.mission.discodeit.exception;

public class PrivateChannelUpdateException extends ChannelException {

  public PrivateChannelUpdateException() {
    super(ErrorCode.PRIVATE_CHANNEL_UPDATE);
  }

  // Map 대신 String channelName을 받도록 수정
  public PrivateChannelUpdateException(String channelName) {
    super(ErrorCode.PRIVATE_CHANNEL_UPDATE, channelName);
  }
}