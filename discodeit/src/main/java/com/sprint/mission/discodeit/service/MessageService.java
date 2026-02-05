package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDeleteRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.message.MessageView;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageView create(MessageCreateRequest request);
    MessageView update(MessageUpdateRequest request);
    MessageView findById(UUID messageId);
    List<MessageView> findAllByChannelId(UUID channelId);
    void delete(MessageDeleteRequest request);
    boolean existsById(UUID messageId);
}
