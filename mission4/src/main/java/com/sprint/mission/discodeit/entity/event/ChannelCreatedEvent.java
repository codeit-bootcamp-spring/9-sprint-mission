package com.sprint.mission.discodeit.entity.event;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import java.util.List;
import java.util.UUID;

/**
 * 채널 생성 SSE 알림용 이벤트. receiverIds가 null이면 전체 브로드캐스트(공개 채널),
 * 값이 있으면 해당 참가자에게만 전송(비공개 채널)합니다.
 */
public record ChannelCreatedEvent(
    ChannelDto channel,
    List<UUID> receiverIds
) {

  public boolean isBroadcast() {
    return receiverIds == null;
  }
}
