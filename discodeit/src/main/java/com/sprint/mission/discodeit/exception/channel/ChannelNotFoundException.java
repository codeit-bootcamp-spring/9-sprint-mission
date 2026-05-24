package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.base.ChannelException;
import com.sprint.mission.discodeit.exception.base.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class ChannelNotFoundException extends ChannelException {

  public ChannelNotFoundException(UUID channelId) {
    super(ErrorCode.CHANNEL_NOT_FOUND, Map.of("ID", channelId));
  }
}
