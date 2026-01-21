package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    Message create(String content, UUID channelId, UUID authorId);

    Message findById(UUID id);

    List<Message> findAll();

    List<Message> findByChannelId(UUID channelId);

    boolean update(UUID id, String content);

    boolean delete(UUID id);
}

