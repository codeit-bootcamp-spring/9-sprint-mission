package com.sprint.mission.discodeit.entity.event;

import com.sprint.mission.discodeit.dto.data.ChannelDto;

/**
 * 채널 수정 SSE 알림용 이벤트. 비공개 채널은 수정이 불가능하므로 항상 전체 브로드캐스트합니다.
 */
public record ChannelUpdatedEvent(
    ChannelDto channel
) {

}
