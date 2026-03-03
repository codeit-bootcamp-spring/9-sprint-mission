package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

/**
 * 메시지 생성 요청 DTO
 * 채널에 새로운 메시지를 작성할 때 사용합니다.
 *
 * @param content 메시지 내용
 * @param channelId 메시지를 보낼 채널 ID
 * @param authorId 메시지 작성자 ID
 */
public record MessageCreateRequest(
        String content,
        UUID channelId,
        UUID authorId
) {
}
