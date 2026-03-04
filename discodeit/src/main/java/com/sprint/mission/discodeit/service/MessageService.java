package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.entity.Message;

import java.util.*;

public interface MessageService {
    Message create(MessageCreateRequest request, List<UUID> attachmentIds);

    void remove(UUID id);

    Message findByID(UUID id);

    List<Message> findAllByChannelId(UUID channelId);

    Message updateContent(UUID id, String newContent);

}
