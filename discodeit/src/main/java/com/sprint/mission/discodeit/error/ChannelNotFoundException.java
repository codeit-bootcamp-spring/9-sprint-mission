package com.sprint.mission.discodeit.error;

import java.util.Map;

public class ChannelNotFoundException extends ChannelException {

  public ChannelNotFoundException(Map<String, Object> details) {
    super(ErrorCode.CHANNEL_NOT_FOUND, details);
  }
}
