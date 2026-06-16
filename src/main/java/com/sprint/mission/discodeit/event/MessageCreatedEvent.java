package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Message;
import java.util.UUID;

/**
 * 채널에 새로운 메시지가 등록되었을 때 발행되는 이벤트.
 * AFTER_COMMIT 이후 lazy 로딩이 불가능하므로,
 * 트랜잭션이 살아있는 시점에 필요한 값만 추출해 보관합니다.
 */
public record MessageCreatedEvent(
    UUID messageId,
    UUID channelId,
    String channelName,
    UUID authorId
) {

  public static MessageCreatedEvent from(Message message) {
    return new MessageCreatedEvent(
        message.getId(),
        message.getChannel().getId(),
        message.getChannel().getName(), // 트랜잭션 내 — lazy 안전
        message.getAuthor().getId()     // 트랜잭션 내 — lazy 안전
    );
  }
}
