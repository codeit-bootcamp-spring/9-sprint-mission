package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;

import java.util.*;

public interface MessageService {
    Message create(CreateMessageRequest request, List<UUID> attachmentIds);

    void remove(UUID id);

    Message findByID(UUID id);

    List<Message> findAllByChannelId(UUID channelId);

    Message updateContent(UUID id, String newContent);

}
