package main.java.com.sprint.mission.discodeit.repository;

import main.java.com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);
    Optional<Message> findId(UUID content);
    List<Message> findAll();
    void deleteById(UUID id);
}
