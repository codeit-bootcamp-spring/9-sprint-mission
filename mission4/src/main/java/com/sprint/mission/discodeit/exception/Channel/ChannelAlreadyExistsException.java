package com.sprint.mission.discodeit.exception.Channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class ChannelAlreadyExistsException extends ChannelException {

  public ChannelAlreadyExistsException(String channelName) {
    super(ErrorCode.DUPLICATE_CHANNEL, Map.of("name", channelName));
  }

}
