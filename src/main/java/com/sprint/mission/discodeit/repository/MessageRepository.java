package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);
    Message find(UUID id);
    List<Message> findAll();
    List<Message> findAllByChannelId(UUID channelId);
    void delete(UUID id);
}
