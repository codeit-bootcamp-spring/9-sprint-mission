package com.sprint.mission.discodeit.exception.domain;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class ChannelUpdateNotAllowedException extends ChannelException {
  public ChannelUpdateNotAllowedException(UUID channelId) {
    super(ErrorCode.CHANNEL_UPDATE_NOT_ALLOWED, Map.of("channelId", channelId));
  }
}
