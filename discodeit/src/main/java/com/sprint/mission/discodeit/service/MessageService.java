package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequestDto;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    Message create(MessageCreateRequestDto dto);
    Message find(UUID messageId);
    List<Message> findByChannelId(UUID channelId);
    Message update(UUID messageId, String newContent);
    void delete(UUID messageId);
}
