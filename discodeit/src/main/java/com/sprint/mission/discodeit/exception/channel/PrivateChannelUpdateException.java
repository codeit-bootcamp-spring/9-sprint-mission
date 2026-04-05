package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.UUID; // ID 타입에 맞춰 수정하세요 (String, Long 등)

public class PrivateChannelUpdateException extends ChannelException {

  public PrivateChannelUpdateException(UUID channelId) {
    super(ErrorCode.PRIVATE_CHANNEL_UPDATE,
        String.format("비공개 채널은 수정할 수 없습니다: Channel ID=%s", channelId));
    addDetail("channelId", channelId);
    addDetail("reason", "PRIVATE_CHANNEL_IMMUTABLE");
  }
}