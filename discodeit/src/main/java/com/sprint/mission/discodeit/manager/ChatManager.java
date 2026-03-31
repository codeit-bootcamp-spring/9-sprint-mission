package com.sprint.mission.discodeit.manager;

import com.sprint.mission.discodeit.dto.response.MessageDto;
import java.util.Optional;
import java.util.UUID;

public interface ChatManager {

  Optional<MessageDto> sendMessage(UUID userId, UUID channelId, String content);

  String getAuthorName(UUID messageId);
}