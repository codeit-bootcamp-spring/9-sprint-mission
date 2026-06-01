package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Message;
import java.util.UUID;

/**
 * 채널에 새로운 메시지가 등록되었을 때 발행되는 이벤트.
 */
public record MessageCreatedEvent(Message message) {

  public UUID channelId() {
    return message.getChannel().getId();
  }

  public UUID messageId() {
    return message.getId();
  }
}
