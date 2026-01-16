package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageSevice {
    Message create(String name, String message, UUID user);

    Message find(UUID userMessage);

    List<Message> findAll();

    Message updateMessage(UUID userMessage, String newmessage);

    void delete(UUID userMessage);
}
