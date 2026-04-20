package com.sprint.mission.discodeit.exception;

public class ChannelException extends DiscodeitException {

  private final String channelName;

  public ChannelException(ErrorCode errorCode) {
    super(errorCode);
    this.channelName = null;
  }

  // Map 대신 채널 이름을 직접 받도록 수정
  public ChannelException(ErrorCode errorCode, String channelName) {
    super(errorCode);
    this.channelName = channelName;
  }

  public String getChannelName() {
    return channelName;
  }
}