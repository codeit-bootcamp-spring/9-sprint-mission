package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ReadStatusAlreadyExistsException extends ReadStatusException {

  public ReadStatusAlreadyExistsException(String userId, String channelId) {
    super(ErrorCode.READ_STATUS_ALREADY_EXISTS, userId, channelId);
  }
}