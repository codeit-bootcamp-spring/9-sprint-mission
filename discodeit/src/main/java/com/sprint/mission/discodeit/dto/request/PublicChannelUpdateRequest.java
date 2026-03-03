package com.sprint.mission.discodeit.dto.request;

/**
 * Public 채널 정보 수정 요청 DTO
 * 채널의 이름과 설명을 변경할 때 사용합니다.
 *
 * @param newName 변경할 채널 이름
 * @param newDescription 변경할 채널 설명
 */
public record PublicChannelUpdateRequest(
        String newName,
        String newDescription
) {
}
