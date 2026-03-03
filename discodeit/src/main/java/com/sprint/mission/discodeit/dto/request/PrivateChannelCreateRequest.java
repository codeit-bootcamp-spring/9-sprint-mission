package com.sprint.mission.discodeit.dto.request;

import java.util.List;
import java.util.UUID;

/**
 * 비공개 채널 생성 요청 DTO
 * PRIVATE 타입의 채널을 생성할 때 사용합니다.
 *
 * @param participantIds 채널에 초대할 사용자 ID 목록
 */
public record PrivateChannelCreateRequest(
        List<UUID> participantIds
) {
}
