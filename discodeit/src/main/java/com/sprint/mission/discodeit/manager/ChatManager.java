package com.sprint.mission.discodeit.manager;

import com.sprint.mission.discodeit.dto.MessageResponse; // 새로운 DTO 임포트
import java.util.Optional;
import java.util.UUID;

public interface ChatManager {
    String getAuthorName(UUID messageId);
    Optional<MessageResponse> sendMessage(UUID userId, UUID channelId, String content);
}