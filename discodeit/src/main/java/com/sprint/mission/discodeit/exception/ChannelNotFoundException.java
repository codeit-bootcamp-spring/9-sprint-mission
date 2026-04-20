package com.sprint.mission.discodeit.exception;

public class ChannelNotFoundException extends ChannelException {

  public ChannelNotFoundException() {
    super(ErrorCode.CHANNEL_NOT_FOUND);
  }

  // Map 대신 String channelName을 받도록 수정
  public ChannelNotFoundException(String channelName) {
    super(ErrorCode.CHANNEL_NOT_FOUND, channelName);
  }
}