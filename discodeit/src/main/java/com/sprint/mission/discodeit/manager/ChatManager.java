package com.sprint.mission.discodeit.manager;

import com.sprint.mission.discodeit.entity.Message;
import java.util.Optional;
import java.util.UUID;

public interface ChatManager {

  Optional<Message> sendMessage(UUID userId, UUID channelId, String content);

  String getAuthorName(UUID messageId);
}