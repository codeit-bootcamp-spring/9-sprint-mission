package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    List<Message> findAllByContentKeyword(String keyword);
    Message save(Message message);
    Optional<Message> findById(UUID id);
    List<Message> findAll();
    List<Message> findByChannelId(UUID channelId);
    void update(Message message);
    boolean delete(UUID id);
}