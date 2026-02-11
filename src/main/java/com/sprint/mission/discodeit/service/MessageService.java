package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.UpdateMessageRequest;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageResponse create(CreateMessageRequest request);
    List<MessageResponse> findAllByChannelId(UUID channelId);
    MessageResponse update(UUID messageId, UpdateMessageRequest request);
    void delete(UUID messageId);
}
