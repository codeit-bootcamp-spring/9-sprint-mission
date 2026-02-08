package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.DTO.Message.MessageCreateRequest;
import com.sprint.mission.discodeit.service.DTO.Message.MessageUpdateRequest;
import com.sprint.mission.discodeit.service.DTO.Message.MessageResponse;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(MessageCreateRequest request);
    Message find(UUID messageId);
    List<MessageResponse> findAllByChannelId(UUID channelId);
    Message update(MessageUpdateRequest request);
    void delete(UUID messageId);
}
