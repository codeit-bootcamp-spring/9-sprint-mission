package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {

    void save (Message message);

    boolean remove(UUID id);

    Optional<Message> findByID(UUID id);

    List<Message> findAll();
}
