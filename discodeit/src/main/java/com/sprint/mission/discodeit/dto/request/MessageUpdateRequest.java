package com.sprint.mission.discodeit.dto.request;

/**
 * 메시지 수정 요청 DTO
 * 기존 메시지의 내용을 변경할 때 사용합니다.
 *
 * @param newContent 변경할 메시지 내용
 */
public record MessageUpdateRequest(
        String newContent
) {
}
