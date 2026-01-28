package com.sprint.mission.discodeit.manager;

import com.sprint.mission.discodeit.dto.MessageDto;
import java.util.Optional;
import java.util.UUID;

public interface ChatManager {
    /**
     * 메시지 ID를 통해 작성자의 표시 이름을 조회합니다.
     */
    String getAuthorName(UUID messageId);

    /**
     * 특정 채널에 메시지를 전송합니다.
     * [수정] 반환 타입을 엔티티에서 DTO(MessageDto.Response)로 변경했습니다.
     */
    Optional<MessageDto.Response> sendMessage(UUID userId, UUID channelId, String content);}