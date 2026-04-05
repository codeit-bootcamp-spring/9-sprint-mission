package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.UUID;

public class MessageNotFoundException extends MessageException {

  public MessageNotFoundException(UUID messageId) {
    super(ErrorCode.MESSAGE_NOT_FOUND,
        String.format("메시지를 찾을 수 없습니다: Message ID=%s", messageId));
    addDetail("messageId", messageId);
  }
}