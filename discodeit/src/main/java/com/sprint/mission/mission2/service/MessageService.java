package com.sprint.mission.mission2.service;

import com.sprint.mission.mission2.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    public Message create(UUID channelId, UUID userId, String content);
    public Message read(UUID id);
    public List<Message> readAll();
    public void update(UUID id, String content);
    public void delete(UUID id);
}