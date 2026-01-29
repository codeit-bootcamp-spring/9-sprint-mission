package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(UUID channaId, UUID senderId, String content);
    Message update(UUID messageId, String content);
    Message findById(UUID messageId);
    List<Message> findAll();
    void delete(UUID messageId);
    boolean existsById(UUID messageId);
}
