package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public interface MessageRepository {

    void save (Message message);

    boolean remove(UUID id);

    Message findByID(UUID id);

    List<Message> findAll();
}
