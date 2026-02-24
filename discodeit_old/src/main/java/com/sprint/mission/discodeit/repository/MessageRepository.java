package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {

    void create(Message message);

    Message findById(UUID id);

    List<Message> findAll();

    List<Message> findByChannelId(UUID channelId);

    boolean update(UUID id, String content);

    boolean delete(UUID id);
}

