package com.sprint.mission.discodeit.manager;

import com.sprint.mission.discodeit.dto.MessageDto;
import java.util.Optional;
import java.util.UUID;

public interface ChatManager {
    String getAuthorName(UUID messageId);
    Optional<MessageDto.Response> sendMessage(UUID userId, UUID channelId, String content);
}