package com.sprint.mission.discodeit.dto.request;

/**
 * 공개 채널 생성 요청 DTO
 * PUBLIC 타입의 채널을 생성할 때 사용합니다.
 *
 * @param name 채널 이름
 * @param description 채널 설명
 */
public record PublicChannelCreateRequest(
        String name,
        String description
) {
}
