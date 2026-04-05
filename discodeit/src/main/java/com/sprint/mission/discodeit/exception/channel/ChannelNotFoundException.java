package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.UUID;

public class ChannelNotFoundException extends ChannelException {

  public ChannelNotFoundException(UUID channelId) {
    super(ErrorCode.CHANNEL_NOT_FOUND,
        String.format("채널을 찾을 수 없습니다: ID=%s", channelId));
    addDetail("channelId", channelId);
  }
}