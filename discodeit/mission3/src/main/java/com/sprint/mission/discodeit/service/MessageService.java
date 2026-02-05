package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.MessageDto;
import com.sprint.mission.discodeit.DTO.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(MessageDto.CreateMessage createMessage);
    MessageDto.findMessage find(UUID messageId);
    List<Message> findAllByChannelId(UUID channelId);
    Message update(MessageDto.UpdateMessage updateMessage);
    void delete(UUID messageId);
}
