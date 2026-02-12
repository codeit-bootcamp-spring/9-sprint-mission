package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    MessageResponse send(MessageCreateRequest request);
    Optional<MessageResponse> findById(UUID id);
    List<MessageResponse> findAllByChannelId(UUID channelId);
    boolean delete(UUID id);
}