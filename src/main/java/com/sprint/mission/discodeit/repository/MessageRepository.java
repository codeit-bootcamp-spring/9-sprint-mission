package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);
    Message findById(UUID Id);
    List<Message> findAll();
    List<Message> findByChannelId(UUID channelId);
    Message update(Message message);
    void delete(UUID Id);
    List<Message> findBySenderId(UUID senderId);
}
