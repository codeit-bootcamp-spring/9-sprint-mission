package com.sprint.mission.discodeit.manager;

import com.sprint.mission.discodeit.entity.Message;
import java.util.UUID;
import java.util.Optional; // 추가

public interface ChatManager {
    String getAuthorName(UUID messageId);

    // [수정] Message 대신 Optional<Message>를 반환하여 안전성을 높임
    Optional<Message> sendMessage(UUID userId, UUID channelId, String content);

    void deleteCategorySafely(UUID categoryId);
}