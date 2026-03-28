package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.response.ResponseMessageDto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    ResponseMessageDto create(String content, UUID channelId, UUID userId, List<UUID> attachmentIdList);

    List<ResponseMessageDto> findAllInChannel(UUID channelId);

    Instant getLastMessageInChannel(UUID channelId);

    List<ResponseMessageDto> findAllForSender(UUID userId);

    ResponseMessageDto updateMessage(UUID id, String content);

    UUID delete(UUID userId, UUID id);

    List<List<UUID>> deleteAll(UUID channelId);

    boolean isPresentMessage(UUID userId, UUID id);
}
