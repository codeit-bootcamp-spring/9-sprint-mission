package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.MessageService.Request.CreateMessageRequest;
import com.sprint.mission.discodeit.entity.Message;

import java.util.*;

public interface MessageService {
    Message create(CreateMessageRequest request);

    void remove(UUID id);

    Message findByID(UUID id);

    List<Message> findAllByChannelId(UUID channelId);

    Message updateContent(UUID id, String newContent);

}
