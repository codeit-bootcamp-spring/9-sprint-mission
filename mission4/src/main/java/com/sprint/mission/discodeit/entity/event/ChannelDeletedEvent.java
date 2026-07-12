package com.sprint.mission.discodeit.entity.event;

import java.util.List;
import java.util.UUID;

/**
 * 채널 삭제 SSE 알림용 이벤트. receiverIds가 null이면 전체 브로드캐스트(공개 채널),
 * 값이 있으면 해당 참가자에게만 전송(비공개 채널)합니다.
 */
public record ChannelDeletedEvent(
    UUID channelId,
    List<UUID> receiverIds
) {

  public boolean isBroadcast() {
    return receiverIds == null;
  }
}
