package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.MessageRequestDto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    UUID create(String content, UUID channelId, UUID userId, List<UUID> attachmentIdList);

    List<MessageRequestDto> findAllInChannel(UUID userId);

    Instant getLastMessageInChannel(UUID channelId);

    List<MessageRequestDto> findAllForSender(UUID userId);

    boolean updateMessage(UUID id, String content);

    UUID delete(UUID userId, UUID id);

    List<List<UUID>> deleteAll(UUID channelId);

    boolean isPresentMessage(UUID userId, UUID id);
}
