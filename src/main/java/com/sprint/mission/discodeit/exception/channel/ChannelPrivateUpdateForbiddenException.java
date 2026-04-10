package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class ChannelPrivateUpdateForbiddenException extends ChannelException {

  public ChannelPrivateUpdateForbiddenException(UUID channelId) {
    super(
        ErrorCode.CHANNEL_PRIVATE_UPDATE_FORBIDDEN,
        Map.of("channelId", channelId.toString())
    );
  }
}

