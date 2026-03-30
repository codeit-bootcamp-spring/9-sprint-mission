package com.sprint.mission.discodeit.exception.ReadStatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class UserAndChannelAlreadyExistException extends ReadStatusException {

  public UserAndChannelAlreadyExistException(UUID userId, UUID channelId) {
    super(ErrorCode.DUPLICATE_USER_AND_CHANNEL, Map.of("user_id", userId, "channel_id", channelId));
  }
}
