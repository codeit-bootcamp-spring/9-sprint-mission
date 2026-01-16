package com.sprint.mission.discodeit.manager;
import com.sprint.mission.discodeit.entity.Message;
import java.util.UUID;

public interface ChatManager {
    String getAuthorName(UUID messageId);
    Message sendMessage(UUID userId, UUID channelId, String content);
    void deleteCategorySafely(UUID categoryId);
}