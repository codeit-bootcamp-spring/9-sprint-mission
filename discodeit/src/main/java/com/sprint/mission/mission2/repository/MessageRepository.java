package com.sprint.mission.mission2.repository;

import com.sprint.mission.mission2.entity.Message;
import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    Message read(UUID id);
    List<Message> readAll();
    void save(Message message);
    void remove(UUID id);
}
